package server.pvptemple.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class VanishCommand implements CommandHandler {
   @Command(
      name = {"vanish", "v"},
      rank = Rank.MOD,
      description = "Vanish instantly."
   )
   public void vanish(Mineman mineman, @Param(name = "rank",defaultTo = "Trainee") Rank rank) {
      if (CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().startsWith("practice-")) {
         mineman.getPlayer().sendMessage(CC.RED + "You cannot vanish in practice!");
      } else if (rank.getPriority() >= mineman.getRank().getPriority()) {
         mineman.getPlayer().sendMessage(CC.RED + "You cannot hide yourself from players with the " + rank.getColor() + rank.getName() + CC.RED + " rank!");
      } else {
         if (mineman.isVanishMode()) {
            mineman.setVanishMode(false);
            Bukkit.getServer().getOnlinePlayers().stream().filter((otherx) -> !otherx.canSee(mineman.getPlayer())).forEach((otherx) -> otherx.showPlayer(mineman.getPlayer()));
            mineman.getPlayer().sendMessage(CC.GREEN + "You are now visible to all players.");
         } else {
            mineman.setVanishMode(true);

            for(Player other : Bukkit.getOnlinePlayers()) {
               if (mineman.getPlayer() != other) {
                  Mineman otherMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(other.getUniqueId());
                  if (otherMineman.getRank().getPriority() <= rank.getPriority()) {
                     other.hidePlayer(mineman.getPlayer());
                  }
               }
            }

            mineman.getPlayer().sendMessage(CC.GREEN + "You are now invisible to players ranked " + rank.getColor() + rank.getName() + CC.GREEN + " and below.");
         }

      }
   }
}
