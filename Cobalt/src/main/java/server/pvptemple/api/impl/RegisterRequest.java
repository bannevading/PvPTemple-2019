package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;
import server.pvptemple.util.finalutil.MapUtil;

public abstract class RegisterRequest implements Request {
   private final String path;

   public String getPath() {
      return "/player/confirmation/" + this.path;
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"path"})
   public RegisterRequest(String path) {
      this.path = path;
   }

   public static final class InsertRequest extends RegisterRequest {
      private final UUID uuid;
      private final String confirmationId;
      private final String emailAddress;

      public InsertRequest(UUID uuid, String confirmationId, String emailAddress) {
         super("insert/" + uuid.toString() + "/" + confirmationId + "?email=" + emailAddress);
         this.uuid = uuid;
         this.confirmationId = confirmationId;
         this.emailAddress = emailAddress;
      }

      public Map<String, Object> toMap() {
         return MapUtil.of("uuid", this.uuid, "confirmationId", this.confirmationId, "emailAddress", this.emailAddress);
      }
   }
}
