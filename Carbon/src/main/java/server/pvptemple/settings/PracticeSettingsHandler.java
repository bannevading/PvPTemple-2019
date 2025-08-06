package server.pvptemple.settings;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.inventory.InventoryUI;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.player.PlayerData;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.ItemUtil;

public class PracticeSettingsHandler implements SettingsHandler {
   private static final List<Integer> PING_RANGES = Arrays.asList(50, 75, 100, 125, 150, 200, 250, 300, -1);
   private final Carbon plugin = Carbon.getInstance();

   public void onCreateSettings(InventoryUI inventoryUI, final Player player) {
      final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
      inventoryUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.REDSTONE, CC.PRIMARY + "Allowing Spectators: " + (playerData.isAllowingSpectators() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
         public void onClick(InventoryClickEvent inventoryClickEvent) {
            player.performCommand("tsp");
            player.closeInventory();
         }
      });
      inventoryUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.DIAMOND_SWORD, CC.PRIMARY + "Allowing Duels: " + (playerData.isAcceptingDuels() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
         public void onClick(InventoryClickEvent inventoryClickEvent) {
            player.performCommand("td");
            player.closeInventory();
         }
      });
      inventoryUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.MAP, CC.PRIMARY + "Sidebar Visibility: " + (playerData.isScoreboardEnabled() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
         public void onClick(InventoryClickEvent inventoryClickEvent) {
            player.performCommand("tsb");
            player.closeInventory();
         }
      });
      inventoryUI.setItem(8, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + "Matchmaking Settings")) {
         public void onClick(InventoryClickEvent inventoryClickEvent) {
            if (!mineman.hasRank(Rank.MASTER)) {
               player.closeInventory();
               player.sendMessage(CC.RED + "Matchmaking Settings are for Master rank and higher.");
            } else {
               player.closeInventory();
               PracticeSettingsHandler.this.openMatchmakingSettings(player, playerData, mineman);
            }
         }
      });
   }

   private void openMatchmakingSettings(final Player player, final PlayerData playerData, final Mineman mineman) {
      InventoryUI matchmakingUI = new InventoryUI(CC.BLUE + CC.BOLD + "Matchmaking Settings", 1, 0);
      matchmakingUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY + (playerData.getPingRange() == -1 ? "Unrestricted" : playerData.getPingRange()))) {
         public void onClick(InventoryClickEvent event) {
            if (!mineman.hasRank(Rank.MASTER)) {
               player.sendMessage(CC.RED + "Ping-based Matchmaking is for Master rank and higher.");
               player.closeInventory();
            } else {
               String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
               int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(), PracticeSettingsHandler.PING_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));
               playerData.setPingRange(range);
               event.getClickedInventory().setItem(0, ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY + (playerData.getPingRange() == -1 ? "Unrestricted" : playerData.getPingRange())));
            }
         }
      });
      player.openInventory(matchmakingUI.getCurrentPage());
   }

   private int handleRangeClick(ClickType clickType, List<Integer> ranges, int current) {
      int min = (Integer)ranges.get(0);
      int max = (Integer)ranges.get(ranges.size() - 1);
      if (clickType == ClickType.LEFT) {
         if (current == max) {
            current = min;
         } else {
            current = (Integer)ranges.get(ranges.indexOf(current) + 1);
         }
      } else if (clickType == ClickType.RIGHT) {
         if (current == min) {
            current = max;
         } else {
            current = (Integer)ranges.get(ranges.indexOf(current) - 1);
         }
      }

      return current;
   }

   private int parseOrDefault(String string, int def) {
      try {
         return Integer.parseInt(string.replace(" ", ""));
      } catch (NumberFormatException var4) {
         return def;
      }
   }
}
