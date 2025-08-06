package server.pvptemple.runnable;

import java.beans.ConstructorProperties;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.Carbon;
import server.pvptemple.match.Match;

public class MatchResetRunnable extends BukkitRunnable {
   private final Carbon plugin = Carbon.getInstance();
   private final Match match;

   public void run() {
      int count = 0;
      if (this.match.getKit().isBuild()) {
         for(Location location : this.match.getPlacedBlockLocations()) {
            ++count;
            if (count > 15) {
               break;
            }

            location.getBlock().setType(Material.AIR);
            this.match.removePlacedBlockLocation(location);
         }
      } else {
         for(BlockState blockState : this.match.getOriginalBlockChanges()) {
            ++count;
            if (count > 15) {
               break;
            }

            blockState.getLocation().getBlock().setType(blockState.getType());
            this.match.removeOriginalBlockChange(blockState);
         }
      }

      if (count < 15) {
         this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
         this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
         this.cancel();
      }

   }

   @ConstructorProperties({"match"})
   public MatchResetRunnable(Match match) {
      this.match = match;
   }
}
