package server.pvptemple.event.player;

import server.pvptemple.event.CancellableEvent;
import server.pvptemple.mineman.Mineman;

public class PrivateMessageEvent extends CancellableEvent {
   private final Mineman from;
   private final Mineman to;
   private String fromDisplayName;
   private String toDisplayName;

   public PrivateMessageEvent(Mineman from, Mineman to, String fromDisplayName, String toDisplayName) {
      this.from = from;
      this.to = to;
      this.fromDisplayName = fromDisplayName;
      this.toDisplayName = toDisplayName;
   }

   public Mineman getFrom() {
      return this.from;
   }

   public Mineman getTo() {
      return this.to;
   }

   public String getFromDisplayName() {
      return this.fromDisplayName;
   }

   public String getToDisplayName() {
      return this.toDisplayName;
   }

   public void setFromDisplayName(String fromDisplayName) {
      this.fromDisplayName = fromDisplayName;
   }

   public void setToDisplayName(String toDisplayName) {
      this.toDisplayName = toDisplayName;
   }
}
