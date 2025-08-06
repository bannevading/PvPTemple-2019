package server.pvptemple.runnable;

import java.beans.ConstructorProperties;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;

public class RematchRunnable implements Runnable {
   private final Carbon plugin = Carbon.getInstance();
   private final UUID playerUUID;

   public void run() {
      Player player = this.plugin.getServer().getPlayer(this.playerUUID);
      if (player != null) {
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (playerData != null && playerData.getPlayerState() == PlayerState.SPAWN && this.plugin.getMatchManager().isRematching(player.getUniqueId()) && this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
            player.getInventory().setItem(3, (ItemStack)null);
            player.getInventory().setItem(6, (ItemStack)null);
            player.updateInventory();
            playerData.setRematchID(-1);
         }

         this.plugin.getMatchManager().removeRematch(this.playerUUID);
      }

   }

   @ConstructorProperties({"playerUUID"})
   public RematchRunnable(UUID playerUUID) {
      this.playerUUID = playerUUID;
   }
}
