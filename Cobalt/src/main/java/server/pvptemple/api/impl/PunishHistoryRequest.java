package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import server.pvptemple.api.request.Request;

public final class PunishHistoryRequest implements Request {
   private final String name;

   public String getPath() {
      return "/punishments/fetch/" + this.name;
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"name"})
   public PunishHistoryRequest(String name) {
      this.name = name;
   }
}
