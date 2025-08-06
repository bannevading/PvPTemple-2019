package server.pvptemple.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.AddIgnoreRequest;
import server.pvptemple.api.impl.IgnoreNamesRequest;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class IgnoreCommand implements CommandHandler {
   @Command(
      name = {"ignore", "unignore"},
      rank = Rank.NORMAL,
      description = "Ignore a player."
   )
   public void ignore(Player player, @Param(name = "player") String target) {
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
      if (target.equals("list")) {
         CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new IgnoreNamesRequest(mineman.getUuid()), (element) -> {
            JsonArray array = element.getAsJsonArray();
            StringBuilder sb = new StringBuilder();
            sb.append(CC.GOLD).append("You are currently ignoring:");

            for(JsonElement element1 : array) {
               JsonObject object = element1.getAsJsonObject();
               sb.append("\n");
               sb.append(CC.GRAY).append(" - ");
               sb.append(CC.WHITE).append(object.get("name").getAsString());
            }

            if (array.size() == 0) {
               sb.append("\n").append(CC.I_GRAY).append("No one.");
            }

            player.sendMessage(sb.toString());
         });
      } else {
         CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new AddIgnoreRequest(mineman.getUuid(), target), (element) -> {
            JsonObject data = element.getAsJsonObject();
            switch (data.get("response").getAsString()) {
               case "cant-ignore":
                  player.sendMessage(CC.RED + "You cannot ignore " + target);
                  break;
               case "player-not-found":
                  player.sendMessage(CC.RED + "Failed to find that player.");
                  break;
               case "success":
                  JsonElement ignoringObject = data.get("target-id");
                  int ignoring = ignoringObject.getAsInt();
                  if (mineman.toggleIgnore(ignoring)) {
                     player.sendMessage(CC.GOLD + "You are now ignoring " + CC.WHITE + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target) + CC.GOLD + ".");
                  } else {
                     player.sendMessage(CC.GOLD + "You are no longer ignoring " + CC.WHITE + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target) + CC.GOLD + ".");
                  }
            }

         });
      }
   }
}
