package server.pvptemple.command.impl.essentials;

import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.rank.Rank;
import server.pvptemple.server.ServerData;
import server.pvptemple.util.finalutil.BungeeUtil;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class JoinCommand extends Command {
   public JoinCommand() {
      super("join");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (sender instanceof ConsoleCommandSender) {
         sender.sendMessage("no u men");
         return false;
      } else {
         Player player = (Player)sender;
         if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /join <server>"));
            return false;
         } else {
            ServerData data = CorePlugin.getInstance().getServerManager().getServerDataByName(args[0]);
            if (data == null) {
               if (PlayerUtil.testPermission(player, Rank.TRAINEE)) {
                  player.sendMessage(Color.translate("&cFailed to find that server."));
               } else {
                  this.sendServers(player);
               }

               return false;
            } else if (!data.isJoinable()) {
               player.sendMessage(Color.translate("&c" + data.getServerName() + " is not able to be joined."));
               return false;
            } else {
               if (PlayerUtil.testPermission(player, Rank.TRAINEE)) {
                  player.sendMessage(Color.translate("&6Sending you to " + data.getServerName() + "..."));
                  BungeeUtil.sendToServer(player, data.getServerName());
               } else {
                  if (!data.getServerName().startsWith("sg-") && !data.getServerName().startsWith("uhc-") && !data.getServerName().startsWith("meetup-")) {
                     this.sendServers(player);
                     return false;
                  }

                  player.sendMessage(Color.translate("&6Sending you to " + data.getServerName() + "..."));
                  BungeeUtil.sendToServer(player, data.getServerName());
               }

               return false;
            }
         }
      }
   }

   private void sendServers(Player player) {
      StringBuilder builder = new StringBuilder();
      CorePlugin.getInstance().getServerManager().getServers().values().stream().filter((server) -> server.isJoinable() && (server.getServerName().startsWith("sg-") || server.getServerName().startsWith("meetup-") || server.getServerName().startsWith("uhc-")) && !server.getServerName().equalsIgnoreCase("uhcgames")).sorted(Comparator.comparing(ServerData::getServerName)).forEach((server) -> {
         if (builder.length() > 0) {
            builder.append("&7, ");
         }

         builder.append("&f").append(server.getServerName());
      });
      player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
      player.sendMessage(Color.translate("&6Available servers:"));
      player.sendMessage(Color.translate(builder.toString()));
      player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
   }
}
