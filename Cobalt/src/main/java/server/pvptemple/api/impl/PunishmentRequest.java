package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.util.Map;
import server.pvptemple.api.request.Request;
import server.pvptemple.util.finalutil.MapUtil;

public final class PunishmentRequest implements Request {
   private final Timestamp expiry;
   private final String ipAddress;
   private final String reason;
   private final String name;
   private final String type;
   private final int playerId;
   private final int id;

   public String getPath() {
      return "/punishments/punish";
   }

   public Map<String, Object> toMap() {
      return MapUtil.of(this.name != null ? "name" : "player-id", this.name != null ? this.name : this.playerId, "ip-address", this.ipAddress == null ? "UNKNOWN" : this.ipAddress, "expiry", this.expiry == null ? "PERM" : this.expiry, "reason", this.reason, "punisher", this.id, "type", this.type);
   }

   @ConstructorProperties({"expiry", "ipAddress", "reason", "name", "type", "playerId", "id"})
   public PunishmentRequest(Timestamp expiry, String ipAddress, String reason, String name, String type, int playerId, int id) {
      this.expiry = expiry;
      this.ipAddress = ipAddress;
      this.reason = reason;
      this.name = name;
      this.type = type;
      this.playerId = playerId;
      this.id = id;
   }
}
