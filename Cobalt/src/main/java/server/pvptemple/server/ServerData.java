package server.pvptemple.server;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

public class ServerData {
   @Getter @Setter
   private String serverName;
   @Setter
   @Getter
   private int onlinePlayers;
   @Setter
   @Getter
   private int maxPlayers;
   @Setter
   @Getter
   private long lastUpdate;
   @Setter
   @Getter
   private boolean whitelisted;
   @Setter
   @Getter
   private boolean joinable = true;
   @Setter
   @Getter
   private JsonObject extra;

}
