package server.pvptemple.events.runner;

import java.util.UUID;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;

public class RunnerPlayer extends EventPlayer {
   private RunnerState state;
   private boolean wasEliminated;

   public RunnerPlayer(UUID uuid, PracticeEvent event) {
      super(uuid, event);
      this.state = RunnerPlayer.RunnerState.WAITING;
      this.wasEliminated = false;
   }

   public void setState(RunnerState state) {
      this.state = state;
   }

   public void setWasEliminated(boolean wasEliminated) {
      this.wasEliminated = wasEliminated;
   }

   public RunnerState getState() {
      return this.state;
   }

   public boolean isWasEliminated() {
      return this.wasEliminated;
   }

   public static enum RunnerState {
      WAITING,
      INGAME,
      ELIMINATED;
   }
}
