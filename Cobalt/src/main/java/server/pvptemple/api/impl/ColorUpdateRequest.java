package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import server.pvptemple.api.request.Request;
import server.pvptemple.util.finalutil.MapUtil;

public final class ColorUpdateRequest implements Request {
   private final String color;
   private final int id;

   public String getPath() {
      return "/player/" + this.id + "/update-color";
   }

   public Map<String, Object> toMap() {
      return MapUtil.of("color", this.color);
   }

   @ConstructorProperties({"color", "id"})
   public ColorUpdateRequest(String color, int id) {
      this.color = color;
      this.id = id;
   }
}
