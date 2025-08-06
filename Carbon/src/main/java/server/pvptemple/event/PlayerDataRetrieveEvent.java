package server.pvptemple.event;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import server.pvptemple.player.PlayerData;

public class PlayerDataRetrieveEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   private final PlayerData playerData;

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   @ConstructorProperties({"playerData"})
   public PlayerDataRetrieveEvent(PlayerData playerData) {
      this.playerData = playerData;
   }
}
