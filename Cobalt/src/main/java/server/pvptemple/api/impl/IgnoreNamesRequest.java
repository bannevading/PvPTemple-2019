package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public final class IgnoreNamesRequest implements Request {
   private final UUID uniqueId;

   public String getPath() {
      return "/player/" + this.uniqueId.toString() + "/ignoring";
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"uniqueId"})
   public IgnoreNamesRequest(UUID uniqueId) {
      this.uniqueId = uniqueId;
   }
}
