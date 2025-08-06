package server.pvptemple.command.impl;

import java.util.Arrays;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.ColorUpdateRequest;
import server.pvptemple.inventory.InventoryUI;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.ItemBuilder;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.BaseCommand;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.WoolUtil;

public class ColorCommand implements CommandHandler {
   private String[] colors = new String[]{"Purple", "Blue", "Light Gray", "Gray", "Pink", "Green", "Light Blue", "Orange", "Red", "Yellow", "Dark Green"};

   @BaseCommand(
      name = {"color", "colour"},
      rank = Rank.BASIC,
      description = "Change your chat prefix color"
   )
   public void usage(Player player) {
      InventoryUI inventory = new InventoryUI(CC.B_BLUE + "Change your name color...", 2);
      Stream.of(this.colors).forEach((colorName) -> {
         String color = this.getColor(colorName);
         inventory.addItem(new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.WOOL)).name(color + colorName).durability(WoolUtil.convertCCToWoolData(color)).lore(Arrays.asList("", "&7Click this item to change your", "&7name color to " + color + player.getName() + "&7.", "", "&a» Click to set «")).build()) {
            public void onClick(InventoryClickEvent event) {
               Player player = (Player)event.getWhoClicked();
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
               if (mineman != null && mineman.isDataLoaded()) {
                  if (mineman.isErrorLoadingData()) {
                     player.sendMessage(CC.RED + "There was an error loading your data. Please relog and try again, or contact an Admin if this error persists.");
                  } else {
                     String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                     String customColor = ColorCommand.this.getColor(name);
                     mineman.setCustomColor(customColor != null ? customColor.replace("&", "§") : "");
                     player.sendMessage(CC.GREEN + "You changed your chat color to " + ColorCommand.this.getColor(name) + name + ".");
                     CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new ColorUpdateRequest(customColor == null ? null : customColor.substring(1, 2), mineman.getId()));
                  }
               } else {
                  player.sendMessage(CC.RED + "Please wait for your data to load.");
               }
            }
         });
      });
      String color = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank().getColor();
      inventory.setItem(17, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.WOOL)).name(color + "Reset Color").durability(WoolUtil.convertCCToWoolData(color)).lore(Arrays.asList("", "&7Click this item to change your", "&7name color to " + color + player.getName() + "&7.", "", "&a» Click to set «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.isDataLoaded()) {
               if (mineman.isErrorLoadingData()) {
                  player.sendMessage(CC.RED + "There was an error loading your data. Please relog and try again, or contact an Admin if this error persists.");
               } else {
                  String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                  String customColor = ColorCommand.this.getColor(name);
                  mineman.setCustomColor(customColor != null ? customColor.replace("&", "§") : "");
                  player.sendMessage(CC.GREEN + "Your chat color is now default.");
                  CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new ColorUpdateRequest(customColor == null ? null : customColor.substring(1, 2), mineman.getId()));
               }
            } else {
               player.sendMessage(CC.RED + "Please wait for your data to load.");
            }
         }
      });
      player.openInventory(inventory.getCurrentPage());
   }

   private String getColor(String colorName) {
      switch (colorName) {
         case "Purple":
            return CC.DARK_PURPLE;
         case "Blue":
            return CC.BLUE;
         case "Light Gray":
            return CC.GRAY;
         case "Gray":
            return CC.DARK_GRAY;
         case "Pink":
            return CC.LIGHT_PURPLE;
         case "Green":
            return CC.GREEN;
         case "Light Blue":
            return CC.AQUA;
         case "Orange":
            return CC.GOLD;
         case "Red":
            return CC.RED;
         case "Yellow":
            return CC.YELLOW;
         case "Dark Green":
            return CC.DARK_GREEN;
         case "Reset Color":
            return null;
         default:
            return "Invalid";
      }
   }
}
