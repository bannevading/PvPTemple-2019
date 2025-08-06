package server.pvptemple.events.sumo;

import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;

public class SumoPlayer extends EventPlayer {
   private SumoState state;
   private BukkitTask fightTask;
   private SumoPlayer fighting;

   public SumoPlayer(UUID uuid, PracticeEvent event) {
      super(uuid, event);
      this.state = SumoPlayer.SumoState.WAITING;
   }

   public void setState(SumoState state) {
      this.state = state;
   }

   public void setFightTask(BukkitTask fightTask) {
      this.fightTask = fightTask;
   }

   public void setFighting(SumoPlayer fighting) {
      this.fighting = fighting;
   }

   public SumoState getState() {
      return this.state;
   }

   public BukkitTask getFightTask() {
      return this.fightTask;
   }

   public SumoPlayer getFighting() {
      return this.fighting;
   }

   public static enum SumoState {
      WAITING,
      PREPARING,
      FIGHTING,
      ELIMINATED;
   }
}
