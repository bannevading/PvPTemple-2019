package server.pvptemple.oldapi;

import java.util.Map;

public interface APIMessage {
   String getChannel();

   Map<String, Object> toMap();
}
