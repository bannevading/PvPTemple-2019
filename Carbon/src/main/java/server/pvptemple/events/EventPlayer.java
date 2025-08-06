package server.pvptemple.events;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class EventPlayer {
   private final UUID uuid;
   private final PracticeEvent event;

   public UUID getUuid() {
      return this.uuid;
   }

   public PracticeEvent getEvent() {
      return this.event;
   }

   @ConstructorProperties({"uuid", "event"})
   public EventPlayer(UUID uuid, PracticeEvent event) {
      this.uuid = uuid;
      this.event = event;
   }
}
