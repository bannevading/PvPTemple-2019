package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public final class IPCheckRequest implements Request {
   private final InetAddress address;
   private final UUID uniqueId;

   public String getPath() {
      return "/player/" + this.uniqueId.toString() + "/ip-check/" + this.address.getHostAddress();
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"address", "uniqueId"})
   public IPCheckRequest(InetAddress address, UUID uniqueId) {
      this.address = address;
      this.uniqueId = uniqueId;
   }
}
