package server.pvptemple.event.match;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import server.pvptemple.match.Match;

public class MatchEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   private final Match match;

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public Match getMatch() {
      return this.match;
   }

   @ConstructorProperties({"match"})
   public MatchEvent(Match match) {
      this.match = match;
   }
}
