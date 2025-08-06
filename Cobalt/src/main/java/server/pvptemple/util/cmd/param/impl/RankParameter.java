package server.pvptemple.util.cmd.param.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;

public class RankParameter extends Parameter<Rank> {
   public Rank transfer(CommandSender sender, String argument) {
      Rank rank = Rank.getByName(argument);
      if (argument.equalsIgnoreCase("")) {
         rank = Rank.NORMAL;
      }

      if (argument.equalsIgnoreCase("self") && sender instanceof Player) {
         rank = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player)sender).getUniqueId()).getRank();
      }

      if (rank == null) {
         sender.sendMessage(CC.RED + "There is no rank with the name '" + argument + "'.");
         return null;
      } else {
         return rank;
      }
   }
}
