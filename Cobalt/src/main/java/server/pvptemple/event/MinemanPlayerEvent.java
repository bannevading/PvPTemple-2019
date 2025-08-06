package server.pvptemple.event;

import java.util.UUID;

import server.pvptemple.event.PlayerEvent;
import server.pvptemple.mineman.Mineman;

public class MinemanPlayerEvent extends PlayerEvent {
   private final Mineman mineman;

   public MinemanPlayerEvent(Mineman mineman) {
      super(mineman.getPlayer());
      this.mineman = mineman;
   }

   public UUID getUniqueId() {
      return this.mineman.getUuid();
   }

   public Mineman getMineman() {
      return this.mineman;
   }
}
