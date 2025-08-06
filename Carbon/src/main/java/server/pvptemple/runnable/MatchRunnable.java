package server.pvptemple.runnable;

import java.beans.ConstructorProperties;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.Carbon;
import server.pvptemple.managers.MatchManager;
import server.pvptemple.managers.PlayerManager;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchState;
import server.pvptemple.util.finalutil.CC;

public class MatchRunnable extends BukkitRunnable {
   private final Carbon plugin = Carbon.getInstance();
   private final Match match;

   public void run() {
      switch (this.match.getMatchState()) {
         case STARTING:
            if (this.match.decrementCountdown() == 0) {
               this.match.setMatchState(MatchState.FIGHTING);
               this.match.broadcastWithSound(CC.GREEN + "The match has started.", Sound.FIREWORK_BLAST);
               if (this.match.isRedrover()) {
                  this.plugin.getMatchManager().pickPlayer(this.match);
               }
            } else {
               String seconds = this.match.getCountdown() > 1 ? "seconds" : "second";
               this.match.broadcastWithSound(CC.YELLOW + "The match starts in " + CC.AQUA + this.match.getCountdown() + CC.YELLOW + " " + seconds + ".", Sound.CLICK);
            }
            break;
         case SWITCHING:
            if (this.match.decrementCountdown() == 0) {
               this.match.getEntitiesToRemove().forEach(Entity::remove);
               this.match.clearEntitiesToRemove();
               this.match.setMatchState(MatchState.FIGHTING);
               this.plugin.getMatchManager().pickPlayer(this.match);
            }
            break;
         case ENDING:
            if (this.match.decrementCountdown() == 0) {
               this.plugin.getTournamentManager().removeTournamentMatch(this.match);
               this.match.getRunnables().forEach((id) -> this.plugin.getServer().getScheduler().cancelTask(id));
               this.match.getEntitiesToRemove().forEach(Entity::remove);
               this.match.getTeams().forEach((team) -> {
                  Stream<Player> var10000 = team.alivePlayers();
                  PlayerManager var10001 = this.plugin.getPlayerManager();
                  var10000.forEach(var10001::sendToSpawnAndReset);
               });
               Stream<Player> var10000 = this.match.spectatorPlayers();
               MatchManager var10001 = this.plugin.getMatchManager();
               var10000.forEach(var10001::removeSpectator);
               this.match.getPlacedBlockLocations().forEach((location) -> location.getBlock().setType(Material.AIR));

               BlockState blockState;
               while((blockState = this.match.getOriginalBlockChanges().pollLast()) != null) {
                  blockState.getLocation().getBlock().setType(blockState.getType());
                  blockState.update(true, false);
               }

               this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
               this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
               this.plugin.getMatchManager().removeMatch(this.match);
               this.cancel();
            }
      }

   }

   @ConstructorProperties({"match"})
   public MatchRunnable(Match match) {
      this.match = match;
   }
}
