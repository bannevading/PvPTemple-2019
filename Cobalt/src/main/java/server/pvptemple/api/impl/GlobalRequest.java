package server.pvptemple.api.impl;

import com.google.common.collect.ImmutableMap;
import java.beans.ConstructorProperties;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public final class GlobalRequest implements Request {
   private final InetAddress address;
   private final UUID uniqueId;
   private final String name;

   public String getPath() {
      return "/player/" + this.uniqueId.toString() + "/global";
   }

   public Map<String, Object> toMap() {
      return ImmutableMap.of("name", this.name, "ip", this.address.getHostAddress());
   }

   @ConstructorProperties({"address", "uniqueId", "name"})
   public GlobalRequest(InetAddress address, UUID uniqueId, String name) {
      this.address = address;
      this.uniqueId = uniqueId;
      this.name = name;
   }
}
