package server.pvptemple.event.match;

import server.pvptemple.match.Match;

public class MatchStartEvent extends MatchEvent {
   public MatchStartEvent(Match match) {
      super(match);
   }
}
