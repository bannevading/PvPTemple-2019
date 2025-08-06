package server.pvptemple.request;

import com.google.common.collect.ImmutableMap;
import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public class InsertMatchRequest implements Request {
   private final UUID matchId;
   private final Integer winners;
   private final Integer losers;
   private final int inventory;
   private final int[] eloBefore;
   private final int[] eloAfter;

   public String getPath() {
      return "/matches/insert/match";
   }

   public Map<String, Object> toMap() {
      return (new ImmutableMap.Builder()).put("match-id", this.matchId.toString()).put("inventory", this.inventory).put("winner-elo-before", this.eloBefore[0]).put("loser-elo-before", this.eloBefore[1]).put("winner-elo-after", this.eloAfter[0]).put("loser-elo-after", this.eloAfter[1]).put("winners", this.winners).put("losers", this.losers).build();
   }

   @ConstructorProperties({"matchId", "winners", "losers", "inventory", "eloBefore", "eloAfter"})
   public InsertMatchRequest(UUID matchId, Integer winners, Integer losers, int inventory, int[] eloBefore, int[] eloAfter) {
      this.matchId = matchId;
      this.winners = winners;
      this.losers = losers;
      this.inventory = inventory;
      this.eloBefore = eloBefore;
      this.eloAfter = eloAfter;
   }
}
