package server.pvptemple.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Text;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class ReplyCommand implements CommandHandler {
   @Command(
      name = {"reply", "r"},
      rank = Rank.NORMAL,
      description = "Reply to a player's message."
   )
   public void reply(Player player, @Text(value = "--==--",name = "message") String message) {
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
      String target = mineman.getLastConversation();
      if (target != null) {
         if (message.equalsIgnoreCase("--==--") || message.trim().equals("")) {
            player.sendMessage(CC.GREEN + "You are currently messaging " + CC.RESET + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target) + CC.GREEN + ".");
            return;
         }

         player.performCommand("msg " + target + " " + message);
      } else {
         player.sendMessage(CC.RED + "You are not messaging anyone right now.");
      }

   }
}
