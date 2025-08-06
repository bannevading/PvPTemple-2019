package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.util.Map;
import server.pvptemple.api.request.Request;
import server.pvptemple.util.finalutil.MapUtil;

public final class StaffInfoRequest implements Request {
   private final Timestamp timestamp;
   private final String name;

   public String getPath() {
      return "/player/" + this.name + "/staff-info";
   }

   public Map<String, Object> toMap() {
      return MapUtil.of("timestamp", this.timestamp == null ? "LIFETIME" : this.timestamp.toString());
   }

   @ConstructorProperties({"timestamp", "name"})
   public StaffInfoRequest(Timestamp timestamp, String name) {
      this.timestamp = timestamp;
      this.name = name;
   }
}
