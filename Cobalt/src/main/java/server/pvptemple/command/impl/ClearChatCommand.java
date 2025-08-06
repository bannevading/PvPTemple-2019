package server.pvptemple.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class ClearChatCommand implements CommandHandler {
   @Command(
      name = {"clearchat", "cc"},
      rank = Rank.TRAINEE,
      description = "Clear the chat."
   )
   public void clear(CommandSender sender) {
      CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
         StringBuilder builder = new StringBuilder();

         for(int i = 0; i < 100; ++i) {
            builder.append("\n");
         }

         String message = builder.toString();

         for(Player player : CorePlugin.getInstance().getServer().getOnlinePlayers()) {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman.getRank().isAbove(Rank.TRAINEE)) {
               mineman.getPlayer().sendMessage(CC.YELLOW + "Public chat has been cleared by " + sender.getName() + ".");
            } else {
               mineman.getPlayer().sendMessage(message);
            }
         }

      });
   }
}
