package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import server.pvptemple.api.request.Request;
import server.pvptemple.util.finalutil.MapUtil;

public final class LogRequest {
   public static final class CommandLogRequest implements Request {
      private final String command;
      private final int id;

      public String getPath() {
         return "/log/command/" + this.id;
      }

      public Map<String, Object> toMap() {
         return MapUtil.of("command", this.command);
      }

      @ConstructorProperties({"command", "id"})
      public CommandLogRequest(String command, int id) {
         this.command = command;
         this.id = id;
      }
   }

   public static final class MessageLogRequest implements Request {
      private final String message;
      private final int id;

      public String getPath() {
         return "/log/message/" + this.id;
      }

      public Map<String, Object> toMap() {
         return MapUtil.of("message", this.message);
      }

      @ConstructorProperties({"message", "id"})
      public MessageLogRequest(String message, int id) {
         this.message = message;
         this.id = id;
      }
   }
}
