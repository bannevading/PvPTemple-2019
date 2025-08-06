package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public final class JoinRequest implements Request {
   private final InetAddress address;
   private final UUID uniqueId;
   private final String name;

   public String getPath() {
      return "/player/" + this.uniqueId.toString() + "/joins/update/" + this.address + "/" + this.name;
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"address", "uniqueId", "name"})
   public JoinRequest(InetAddress address, UUID uniqueId, String name) {
      this.address = address;
      this.uniqueId = uniqueId;
      this.name = name;
   }
}
