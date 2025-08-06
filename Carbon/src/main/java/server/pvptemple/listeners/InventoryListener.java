package server.pvptemple.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;

public class InventoryListener implements Listener {
   private final Carbon plugin = Carbon.getInstance();

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      if (!player.getGameMode().equals(GameMode.CREATIVE)) {
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (playerData.getPlayerState() == PlayerState.SPAWN || playerData.getPlayerState() == PlayerState.EVENT && player.getItemInHand() != null && player.getItemInHand().getType() == Material.COMPASS) {
            event.setCancelled(true);
         }
      }

   }
}
