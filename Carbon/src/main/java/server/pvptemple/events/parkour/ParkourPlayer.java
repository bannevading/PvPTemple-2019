package server.pvptemple.events.parkour;

import java.util.UUID;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.util.CustomLocation;

public class ParkourPlayer extends EventPlayer {
   private ParkourState state;
   private CustomLocation lastCheckpoint;
   private int checkpointId;

   public ParkourPlayer(UUID uuid, PracticeEvent event) {
      super(uuid, event);
      this.state = ParkourPlayer.ParkourState.WAITING;
   }

   public void setState(ParkourState state) {
      this.state = state;
   }

   public void setLastCheckpoint(CustomLocation lastCheckpoint) {
      this.lastCheckpoint = lastCheckpoint;
   }

   public void setCheckpointId(int checkpointId) {
      this.checkpointId = checkpointId;
   }

   public ParkourState getState() {
      return this.state;
   }

   public CustomLocation getLastCheckpoint() {
      return this.lastCheckpoint;
   }

   public int getCheckpointId() {
      return this.checkpointId;
   }

   public static enum ParkourState {
      WAITING,
      INGAME;
   }
}
