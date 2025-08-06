package server.pvptemple.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.kit.Kit;
import server.pvptemple.player.PlayerData;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;

public class ResetStatsCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public ResetStatsCommand() {
      super("resetstats");
      this.setUsage(CC.RED + "Usage: /resetstats [player]");
   }

   public boolean execute(CommandSender commandSender, String s, String[] args) {
      if (commandSender instanceof Player && !PlayerUtil.testPermission(commandSender, Rank.ADMIN)) {
         return true;
      } else if (args.length == 0) {
         commandSender.sendMessage(CC.RED + "Usage: /resetstats <player>");
         return true;
      } else {
         Player target = this.plugin.getServer().getPlayer(args[0]);
         if (target == null) {
            commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
         } else {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());

            for(Kit kit : this.plugin.getKitManager().getKits()) {
               playerData.setElo(kit.getName(), 1000);
               playerData.setLosses(kit.getName(), 0);
               playerData.setWins(kit.getName(), 0);
            }

            commandSender.sendMessage(CC.GREEN + "You reset " + CC.GREEN + target.getName() + CC.GREEN + "'s stats.");
            return true;
         }
      }
   }
}
