package server.pvptemple.entity;

import org.bukkit.entity.Player;
import server.pvptemple.event.player.PlayerInteractFakeEntityEvent;

public interface EntityInteraction {
   boolean interact(Player var1, PlayerInteractFakeEntityEvent var2);
}
