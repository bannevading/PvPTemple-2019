package server.pvptemple.request;

import com.google.common.collect.ImmutableMap;
import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONObject;
import server.pvptemple.api.request.Request;

public class InventorySnapshotRequest implements Request {
   private final JSONObject inventoryA;
   private final JSONObject inventoryB;
   private final UUID matchId;

   public String getPath() {
      return "/matches/insert/inventory";
   }

   public Map<String, Object> toMap() {
      return ImmutableMap.of("match-id", this.matchId.toString(), "inventory-a", this.inventoryA.toJSONString(), "inventory-b", this.inventoryB.toJSONString());
   }

   @ConstructorProperties({"inventoryA", "inventoryB", "matchId"})
   public InventorySnapshotRequest(JSONObject inventoryA, JSONObject inventoryB, UUID matchId) {
      this.inventoryA = inventoryA;
      this.inventoryB = inventoryB;
      this.matchId = matchId;
   }
}
