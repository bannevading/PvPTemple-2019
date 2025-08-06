package server.pvptemple.command.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Timestamp;
import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.abstr.AbstractBukkitCallback;
import server.pvptemple.api.impl.PlayerRequest;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;

public class BanInfoCommand implements CommandHandler {
   private static void appendBuilder(StringBuilder sb, boolean state, long time, JsonElement reason) {
      sb.append(CC.SECONDARY);
      if (time != 0L && state) {
         String timeStamp = (new Timestamp(time)).toString();
         timeStamp = timeStamp.substring(0, timeStamp.indexOf("."));
         sb.append(CC.RESET).append("Yes").append(CC.YELLOW).append(" until ").append(CC.RESET).append(timeStamp);
      } else if (state) {
         sb.append(CC.RESET).append("Forever");
      } else {
         sb.append(CC.RESET).append("No");
      }

      if (reason != null && !reason.isJsonNull()) {
         sb.append(CC.YELLOW).append(" for ").append(CC.RESET).append(reason.getAsString());
      }

   }

   @Command(
      name = {"staffinfo", "sa"},
      rank = Rank.ADMIN,
      description = "Get info on a staff member's activity"
   )
   public void onStaffInfo(Player sender, @Param(name = "target") String name, @Param(name = "time",defaultTo = "lifetime") String time) {
      sender.sendMessage(CC.YELLOW + "TODO: Revert this command");
   }

   @Command(
      name = {"baninfo", "bminfo", "binfo"},
      rank = Rank.TRAINEE,
      description = "Get info on a player's punishments."
   )
   public void onBanInfo(final Player sender, @Param(name = "target") final String name) {
      (new BukkitRunnable() {
         public void run() {
            CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PlayerRequest.BanInfoRequest(CorePlugin.getRequestNameOrUUID(name)), new AbstractBukkitCallback() {
               public void callback(JsonElement element) {
                  JsonObject data = element.getAsJsonObject();
                  String response = data.get("response").getAsString();
                  if (response.equalsIgnoreCase("player-not-found")) {
                     sender.sendMessage(CC.RED + "Failed to find that player.");
                  } else {
                     String rName = data.get("name").getAsString();
                     JsonElement blacklistReason = data.get("blacklist-reason");
                     JsonElement muteReason = data.get("mute-reason");
                     JsonElement banReason = data.get("ban-reason");
                     boolean blacklisted = data.get("blacklisted").getAsBoolean();
                     boolean banned = data.get("banned").getAsBoolean();
                     boolean muted = data.get("muted").getAsBoolean();
                     long muteTime = data.get("mute-time").getAsLong();
                     long banTime = data.get("ban-time").getAsLong();
                     StringBuilder sb = new StringBuilder(CC.GOLD + "Punishment status of: " + CC.RESET + rName + CC.PRIMARY + "\n\n");
                     sb.append(CC.YELLOW).append("Muted: ");
                     BanInfoCommand.appendBuilder(sb, muted, muteTime, muteReason);
                     sb.append("\n");
                     sb.append(CC.YELLOW).append("Banned: ");
                     BanInfoCommand.appendBuilder(sb, banned, banTime, banReason);
                     sb.append("\n");
                     sb.append(CC.YELLOW).append("Blacklisted: ").append(CC.RESET).append(blacklisted ? "Yes" : "No");
                     if (blacklistReason != null && !blacklistReason.isJsonNull()) {
                        sb.append(CC.YELLOW).append(" for ").append(CC.RESET).append(blacklistReason.getAsString());
                     }

                     sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
                     sender.sendMessage(sb.toString());
                     sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
                  }
               }

               public void onError(String message) {
                  super.onError(message);
                  sender.sendMessage(MessageFormat.format("{0}Something went wrong while fetching the ban information of ''{1}''.", CC.RED, name));
               }
            });
         }
      }).runTaskAsynchronously(CorePlugin.getInstance());
   }
}
