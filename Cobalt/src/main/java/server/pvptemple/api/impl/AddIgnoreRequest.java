package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public final class AddIgnoreRequest implements Request {
   private final UUID uniqueId;
   private final String name;

   public String getPath() {
      return "/player/" + this.uniqueId.toString() + "/ignore/" + this.name;
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"uniqueId", "name"})
   public AddIgnoreRequest(UUID uniqueId, String name) {
      this.uniqueId = uniqueId;
      this.name = name;
   }
}
