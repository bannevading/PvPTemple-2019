package server.pvptemple.command.impl;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.redis.CoreRedisManager;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Text;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class HostChatCommand implements CommandHandler {
   @Command(
      name = {"host", "hc", "hchat", "hostchat"},
      rank = Rank.HOST,
      description = "Enter host chat."
   )
   public void onDevChat(final Mineman player, @Text(value = "--==--",name = "message") final String message) {
      if (!message.equalsIgnoreCase("--==--") && !ChatColor.stripColor(message).trim().equals("")) {
         (new BukkitRunnable() {
            public void run() {
               CoreRedisManager.handleMessage(player, message, Mineman.ChatType.HOST);
            }
         }).runTaskAsynchronously(CorePlugin.getInstance());
      } else if (player.getChatType() == Mineman.ChatType.HOST) {
         player.setChatType(Mineman.ChatType.NORMAL);
         player.getPlayer().sendMessage(CC.RED + "You have left host chat.");
      } else {
         player.setChatType(Mineman.ChatType.HOST);
         player.getPlayer().sendMessage(CC.GREEN + "You have entered host chat.");
      }

   }
}
