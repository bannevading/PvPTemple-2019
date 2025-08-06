package server.pvptemple.command.impl.essentials;

import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class RenameCommand extends Command {
   public RenameCommand() {
      super("rename");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /rename <message>"));
            return false;
         } else {
            ItemStack stack = player.getItemInHand();
            if (stack != null && !stack.getType().equals(Material.AIR)) {
               StringBuilder message = new StringBuilder();
               Stream.of(args).forEach((arg) -> message.append(arg).append(" "));
               stack.getItemMeta().setDisplayName(Color.translate(message.toString()));
               player.updateInventory();
               player.sendMessage(Color.translate("&6Renaming your &f" + StringUtils.capitalize(stack.getType().name().toLowerCase().replace("_", "")) + " &6to " + message + "&6."));
               return false;
            } else {
               player.sendMessage(Color.translate("&cYou must hold an item to do this."));
               return false;
            }
         }
      }
   }
}
