package server.pvptemple.events.lms;

import java.util.UUID;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;

public class LMSPlayer extends EventPlayer {
   private LMSState state;

   public LMSPlayer(UUID uuid, PracticeEvent event) {
      super(uuid, event);
      this.state = LMSPlayer.LMSState.WAITING;
   }

   public void setState(LMSState state) {
      this.state = state;
   }

   public LMSState getState() {
      return this.state;
   }

   public static enum LMSState {
      WAITING,
      FIGHTING,
      ELIMINATED;
   }
}
