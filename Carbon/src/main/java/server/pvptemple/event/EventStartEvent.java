package server.pvptemple.event;

import java.beans.ConstructorProperties;
import server.pvptemple.events.PracticeEvent;

public class EventStartEvent extends BaseEvent {
   private final PracticeEvent event;

   public PracticeEvent getEvent() {
      return this.event;
   }

   @ConstructorProperties({"event"})
   public EventStartEvent(PracticeEvent event) {
      this.event = event;
   }
}
