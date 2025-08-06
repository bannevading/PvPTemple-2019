package server.pvptemple.event.player;

import org.bukkit.entity.Player;
import server.pvptemple.entity.wrapper.PlayerWrapper;
import server.pvptemple.event.PlayerEvent;

public class PlayerInteractFakeEntityEvent extends PlayerEvent {
   private final PlayerWrapper playerWrapper;

   public PlayerInteractFakeEntityEvent(Player player, PlayerWrapper playerWrapper) {
      super(player);
      this.playerWrapper = playerWrapper;
   }

   public PlayerWrapper getPlayerWrapper() {
      return this.playerWrapper;
   }
}
