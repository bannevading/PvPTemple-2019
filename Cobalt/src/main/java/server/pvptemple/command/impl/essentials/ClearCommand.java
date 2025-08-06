package server.pvptemple.command.impl.essentials;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class ClearCommand extends Command {
   public ClearCommand() {
      super("clear");
      this.setAliases(Arrays.asList("ci", "clearinventory", "cleari", "cinventory"));
      this.setUsage(Color.translate("&cUsage: /clear <player>"));
      this.setDescription("Clear inventories of players.");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(player, Rank.MOD)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length == 0) {
            player.getInventory().clear();
            player.getInventory().setArmorContents((ItemStack[])null);
            player.updateInventory();
            player.sendMessage(Color.translate("&6You have cleared your inventory."));
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
               target.getInventory().clear();
               target.getInventory().setArmorContents((ItemStack[])null);
               target.updateInventory();
               Mineman pmineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
               String pcolor = pmineman.getCustomColor() != null && !pmineman.getCustomColor().isEmpty() && pmineman.hasRank(Rank.BASIC) ? pmineman.getCustomColor() : pmineman.getRank().getColor();
               target.sendMessage(Color.translate("&6Your inventory was cleared by " + pcolor + player.getName() + "&6."));
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
               String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
               player.sendMessage(Color.translate("&6You have cleared inventory of " + color + target.getName() + "&6."));
               return false;
            }
         }
      }
   }
}
