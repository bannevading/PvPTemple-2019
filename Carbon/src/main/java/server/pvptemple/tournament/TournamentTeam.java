package server.pvptemple.tournament;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.team.KillableTeam;

public class TournamentTeam extends KillableTeam {
   private final Map<UUID, String> playerNames = new HashMap();

   public TournamentTeam(UUID leader, List<UUID> players) {
      super(leader, players);

      for(UUID playerUUID : players) {
         this.playerNames.put(playerUUID, this.plugin.getServer().getPlayer(playerUUID).getName());
      }

   }

   public void broadcast(String message) {
      this.alivePlayers().forEach((player) -> player.sendMessage(message));
   }

   public String getPlayerName(UUID playerUUID) {
      return (String)this.playerNames.get(playerUUID);
   }

   public Map<UUID, String> getPlayerNames() {
      return this.playerNames;
   }
}
