package server.pvptemple.event;

import java.beans.ConstructorProperties;
import java.util.UUID;

import server.pvptemple.event.BaseEvent;
import server.pvptemple.mineman.Mineman;

public class MinemanEvent extends BaseEvent {
   private final Mineman mineman;

   public UUID getUniqueId() {
      return this.mineman.getUuid();
   }

   public Mineman getMineman() {
      return this.mineman;
   }

   @ConstructorProperties({"mineman"})
   public MinemanEvent(Mineman mineman) {
      this.mineman = mineman;
   }
}
