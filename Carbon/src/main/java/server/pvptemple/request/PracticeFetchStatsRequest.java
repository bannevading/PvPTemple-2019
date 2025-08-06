package server.pvptemple.request;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public class PracticeFetchStatsRequest implements Request {
   private final UUID playerUuid;

   public String getPath() {
      return "/practice/" + this.playerUuid.toString();
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"playerUuid"})
   public PracticeFetchStatsRequest(UUID playerUuid) {
      this.playerUuid = playerUuid;
   }
}
