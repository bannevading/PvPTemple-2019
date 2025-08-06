package server.pvptemple.event;

import java.util.UUID;
import org.bukkit.entity.Player;
import server.pvptemple.event.BaseEvent;

public class PlayerEvent extends BaseEvent {
   private Player player;

   public PlayerEvent(Player player) {
      this.player = player;
   }

   public Player getPlayer() {
      return this.player;
   }

   public UUID getUniqueId() {
      return this.player.getUniqueId();
   }
}
