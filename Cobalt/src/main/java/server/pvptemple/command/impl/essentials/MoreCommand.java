package server.pvptemple.command.impl.essentials;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class MoreCommand extends Command {
   public MoreCommand() {
      super("more");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else {
            ItemStack stack = player.getItemInHand();
            if (stack != null && !stack.getType().equals(Material.AIR)) {
               stack.setAmount(stack.getMaxStackSize());
               player.updateInventory();
               player.sendMessage(Color.translate("&6You have stacked your &f" + StringUtils.capitalize(stack.getType().name().toLowerCase().replace("_", "")) + "&6."));
               return false;
            } else {
               player.sendMessage(Color.translate("&cYou must hold an item to do this."));
               return false;
            }
         }
      }
   }
}
