package server.pvptemple.event.bungee;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import server.pvptemple.event.PlayerEvent;

public class BungeeConnectEvent extends PlayerEvent implements Cancellable {
   private boolean cancelled;
   private String server;

   public BungeeConnectEvent(Player player, String server) {
      super(player);
      this.server = server;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public String getServer() {
      return this.server;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public void setServer(String server) {
      this.server = server;
   }
}
