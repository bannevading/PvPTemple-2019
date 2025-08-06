package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import server.pvptemple.api.request.Request;

public final class AnnouncementRequest implements Request {
   private final String server;

   public String getPath() {
      return "/server/" + this.server + "/announcements/";
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"server"})
   public AnnouncementRequest(String server) {
      this.server = server;
   }
}
