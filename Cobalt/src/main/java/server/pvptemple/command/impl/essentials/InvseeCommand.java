package server.pvptemple.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class InvseeCommand extends Command {
   public InvseeCommand() {
      super("invsee");
      this.setUsage(Color.translate("&cUsage: /invsee <player>"));
      this.setDescription("See players inventories.");
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
            player.sendMessage(this.usageMessage);
            return false;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            } else {
               player.sendMessage(Color.translate("&6Viewing the inventory of: " + target.getDisplayName() + "&6."));
               this.getInventory(player, target);
               return false;
            }
         }
      }
   }

   private void getInventory(Player player, Player target) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 54, "Inventory: " + target.getDisplayName());
      inventory.setContents(target.getInventory().getContents());
      ItemStack[] armor = target.getInventory().getArmorContents();
      inventory.setItem(45, armor[0]);
      inventory.setItem(46, armor[1]);
      inventory.setItem(47, armor[2]);
      inventory.setItem(48, armor[3]);
      player.openInventory(inventory);
   }
}
