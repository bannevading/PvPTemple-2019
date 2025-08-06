package server.pvptemple.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class FeedCommand extends Command {
   public FeedCommand() {
      super("feed");
      this.setUsage(Color.translate("&cUsage: /feed <player>"));
      this.setDescription("Feed players.");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length == 0) {
            player.setFoodLevel(20);
            player.setSaturation(10.0F);
            player.sendMessage(Color.translate("&6You have fed yourself."));
            return false;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            } else {
               target.setFoodLevel(20);
               target.setSaturation(10.0F);
               target.sendMessage(Color.translate("&6You have been fed by " + player.getDisplayName() + "&6."));
               player.sendMessage(Color.translate("&6You have fed " + target.getDisplayName() + "&6."));
               return false;
            }
         }
      }
   }
}
