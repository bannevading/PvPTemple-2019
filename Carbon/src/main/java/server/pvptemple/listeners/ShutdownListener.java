package server.pvptemple.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import server.pvptemple.Carbon;
import server.pvptemple.event.PreShutdownEvent;
import server.pvptemple.match.Match;
import server.pvptemple.player.PlayerData;

public class ShutdownListener implements Listener {
   private final Carbon plugin = Carbon.getInstance();

   @EventHandler
   public void onPreShutdown(PreShutdownEvent event) {
      this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
         for(PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
            this.plugin.getPlayerManager().saveData(playerData);
         }

      });

      for(Match match : this.plugin.getMatchManager().getMatches().values()) {
         match.getPlacedBlockLocations().forEach((location) -> location.getBlock().setType(Material.AIR));

         BlockState blockState;
         while((blockState = (BlockState)match.getOriginalBlockChanges().pollLast()) != null) {
            blockState.update(true, false);
         }

         match.getEntitiesToRemove().forEach(Entity::remove);
      }

   }
}
