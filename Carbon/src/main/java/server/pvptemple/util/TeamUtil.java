package server.pvptemple.util;

import java.util.UUID;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.team.KillableTeam;
import server.pvptemple.tournament.TournamentTeam;
import server.pvptemple.util.finalutil.CC;

public class TeamUtil {
   public static String getNames(KillableTeam team) {
      StringBuilder names = new StringBuilder();

      for(int i = 0; i < team.getPlayers().size(); ++i) {
         UUID teammateUUID = (UUID)team.getPlayers().get(i);
         Player teammate = Carbon.getInstance().getServer().getPlayer(teammateUUID);
         String name = "";
         if (teammate == null) {
            if (team instanceof TournamentTeam) {
               name = ((TournamentTeam)team).getPlayerName(teammateUUID);
            }
         } else {
            name = teammate.getName();
         }

         int players = team.getPlayers().size();
         if (teammate != null) {
            names.append(CC.SECONDARY).append(name).append(CC.PRIMARY).append(players - 1 == i ? "" : (players - 2 == i ? (players > 2 ? "," : "") + " and " : ", "));
         }
      }

      return names.toString();
   }
}
