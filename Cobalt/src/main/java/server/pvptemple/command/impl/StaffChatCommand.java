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

public class StaffChatCommand implements CommandHandler {
   private static final String DEFAULT_BLANK = " §§ ";

   @Command(
      name = {"staffchat", "sc"},
      rank = Rank.TRAINEE,
      description = "Enter staff chat."
   )
   public void staffChat(final Mineman player, @Text(name = "message",value = " §§ ") final String message) {
      if (!message.equalsIgnoreCase(" §§ ") && !ChatColor.stripColor(message).trim().equals("")) {
         (new BukkitRunnable() {
            public void run() {
               CoreRedisManager.handleMessage(player, message, Mineman.ChatType.STAFF);
            }
         }).runTaskAsynchronously(CorePlugin.getInstance());
      } else if (player.getChatType() == Mineman.ChatType.STAFF) {
         player.setChatType(Mineman.ChatType.NORMAL);
         player.getPlayer().sendMessage(CC.RED + "You have left staff chat.");
      } else {
         player.setChatType(Mineman.ChatType.STAFF);
         player.getPlayer().sendMessage(CC.GREEN + "You have entered staff chat");
      }

   }
}
