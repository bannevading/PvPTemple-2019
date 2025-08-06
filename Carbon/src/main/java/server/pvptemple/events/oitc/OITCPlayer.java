package server.pvptemple.events.oitc;

import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;

public class OITCPlayer extends EventPlayer {
   private OITCState state;
   private int score;
   private int lives;
   private BukkitTask respawnTask;
   private OITCPlayer lastKiller;

   public OITCPlayer(UUID uuid, PracticeEvent event) {
      super(uuid, event);
      this.state = OITCPlayer.OITCState.WAITING;
      this.score = 0;
      this.lives = 5;
   }

   public void setState(OITCState state) {
      this.state = state;
   }

   public void setScore(int score) {
      this.score = score;
   }

   public void setLives(int lives) {
      this.lives = lives;
   }

   public void setRespawnTask(BukkitTask respawnTask) {
      this.respawnTask = respawnTask;
   }

   public void setLastKiller(OITCPlayer lastKiller) {
      this.lastKiller = lastKiller;
   }

   public OITCState getState() {
      return this.state;
   }

   public int getScore() {
      return this.score;
   }

   public int getLives() {
      return this.lives;
   }

   public BukkitTask getRespawnTask() {
      return this.respawnTask;
   }

   public OITCPlayer getLastKiller() {
      return this.lastKiller;
   }

   public static enum OITCState {
      WAITING,
      PREPARING,
      FIGHTING,
      RESPAWNING,
      ELIMINATED;
   }
}
