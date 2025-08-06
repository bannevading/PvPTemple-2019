package server.pvptemple.command.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import server.pvptemple.CorePlugin;
import server.pvptemple.inventory.InventoryUI;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.settings.SettingsHandler;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.ItemUtil;

public class SettingsCommand implements CommandHandler {
   @Command(
      name = {"settings"},
      rank = Rank.NORMAL
   )
   public void settings(Mineman mineman) {
      final Player player = mineman.getPlayer();
      InventoryUI ui = new InventoryUI(CC.B_BLUE + "Edit settings...", 2);
      ui.setOffset(1);
      ui.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BOOK_AND_QUILL, CC.PRIMARY + "Private Messages: " + (mineman.isCanSeeMessages() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
         public void onClick(InventoryClickEvent event) {
            player.performCommand("tpm");
            player.closeInventory();
         }
      });
      ui.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "Global Chat: " + (mineman.isChatEnabled() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
         public void onClick(InventoryClickEvent event) {
            player.performCommand("tgc");
            player.closeInventory();
         }
      });

      for(SettingsHandler settingsHandler : CorePlugin.getInstance().getSettingsManager().getSettingsHandlers()) {
         settingsHandler.onCreateSettings(ui, player);
      }

      player.openInventory(ui.getCurrentPage());
   }
}
