package server.pvptemple.command.impl;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.sql.Timestamp;
import org.bukkit.command.CommandSender;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.PunishHistoryRequest;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Flag;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.HttpUtil;

public class PunishmentHistoryCommand implements CommandHandler {
   @Command(
      name = {"history", "hist"},
      rank = Rank.TRAINEE,
      description = "Get a player's punishment history."
   )
   public void history(CommandSender sender, @Param(name = "target") String name, @Flag(name = "u") boolean upload) {
      CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PunishHistoryRequest(name), (data) -> {
         JsonArray array = data.getAsJsonArray();
         if (array.size() == 0) {
            sender.sendMessage(CC.RED + "Player has not been punished.");
         } else {
            StringBuilder sb = new StringBuilder(CC.YELLOW + "Viewing punishment history for: " + CC.GRAY + name + "\n");

            for(JsonElement element : array) {
               JsonObject object = element.getAsJsonObject();
               sb.append(CC.GRAY).append("[").append(new Timestamp(object.get("timestamp").getAsLong())).append("] ").append(CC.GOLD).append("[").append(object.get("type").getAsString().toUpperCase()).append("] ");
               if (upload) {
                  sb.append("\t");
               }

               sb.append(CC.RED).append("Added by ").append(object.get("punisher").getAsString()).append(" for \"").append(CC.YELLOW).append(object.get("reason").getAsString()).append(CC.RED).append("\"").append(CC.RED);
               JsonElement duration = object.get("expiry");
               if (duration != null && !(duration instanceof JsonNull)) {
                  if (upload) {
                     sb.append("\t");
                  }

                  sb.append(" until ").append(CC.GRAY).append(new Timestamp(duration.getAsLong()));
               }

               sb.append("\n");
            }

            if (upload) {
               CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> sender.sendMessage(CC.PRIMARY + "Player " + CC.SECONDARY + name + CC.PRIMARY + "'s Punishment History: " + CC.SECONDARY + "https://www.hastebin.com/" + HttpUtil.getHastebin(sb.toString()) + CC.PRIMARY + "."));
            } else {
               sender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 52));
               sender.sendMessage(CC.PRIMARY + sb.toString());
               sender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 52));
            }

         }
      });
   }
}
