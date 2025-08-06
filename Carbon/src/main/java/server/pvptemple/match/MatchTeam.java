package server.pvptemple.match;

import java.util.List;
import java.util.UUID;
import server.pvptemple.team.KillableTeam;

public class MatchTeam extends KillableTeam {
   private final List<Integer> playerIds;
   private final int teamID;

   public MatchTeam(UUID leader, List<UUID> players, List<Integer> playerIds, int teamID) {
      super(leader, players);
      this.playerIds = playerIds;
      this.teamID = teamID;
   }

   public List<Integer> getPlayerIds() {
      return this.playerIds;
   }

   public int getTeamID() {
      return this.teamID;
   }
}
