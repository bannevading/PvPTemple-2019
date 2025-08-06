package server.pvptemple.events.corners;

import java.util.UUID;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;

public class FourCornersPlayer extends EventPlayer {
   private FourCornerState state;
   private boolean wasEliminated;

   public FourCornersPlayer(UUID uuid, PracticeEvent event) {
      super(uuid, event);
      this.state = FourCornersPlayer.FourCornerState.WAITING;
      this.wasEliminated = false;
   }

   public void setState(FourCornerState state) {
      this.state = state;
   }

   public void setWasEliminated(boolean wasEliminated) {
      this.wasEliminated = wasEliminated;
   }

   public FourCornerState getState() {
      return this.state;
   }

   public boolean isWasEliminated() {
      return this.wasEliminated;
   }

   public static enum FourCornerState {
      WAITING,
      INGAME,
      ELIMINATED;
   }
}
