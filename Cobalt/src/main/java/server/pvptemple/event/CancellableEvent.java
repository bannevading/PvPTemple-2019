package server.pvptemple.event;

import org.bukkit.event.Cancellable;
import server.pvptemple.event.BaseEvent;

public class CancellableEvent extends BaseEvent implements Cancellable {
   private boolean cancelled;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
