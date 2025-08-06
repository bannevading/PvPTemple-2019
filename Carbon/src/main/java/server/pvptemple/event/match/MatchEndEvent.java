package server.pvptemple.event.match;

import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;

public class MatchEndEvent extends MatchEvent {
   private final MatchTeam winningTeam;
   private final MatchTeam losingTeam;

   public MatchEndEvent(Match match, MatchTeam winningTeam, MatchTeam losingTeam) {
      super(match);
      this.winningTeam = winningTeam;
      this.losingTeam = losingTeam;
   }

   public MatchTeam getWinningTeam() {
      return this.winningTeam;
   }

   public MatchTeam getLosingTeam() {
      return this.losingTeam;
   }
}
