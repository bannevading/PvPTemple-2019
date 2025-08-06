package server.pvptemple.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class FlyCommand extends Command {
   public FlyCommand() {
      super("fly");
      this.setUsage(Color.translate("&cUsage: /fly <player>"));
      this.setDescription("Feed players.");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(player, Rank.TRAINEE)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length == 0) {
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(Color.translate(player.getAllowFlight() ? "&aYou can now fly." : "&cYou can no longer fly."));
            return false;
         } else if (!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            } else {
               target.setAllowFlight(!target.getAllowFlight());
               target.sendMessage(Color.translate(target.getAllowFlight() ? "&aYou can now fly." : "&cYou can no longer fly."));
               player.sendMessage(Color.translate(target.getAllowFlight() ? "&aYou have enabled fly of " + target.getDisplayName() + "&a." : "&cYou have disabled fly of " + target.getDisplayName() + "&c."));
               return false;
            }
         }
      }
   }
}
