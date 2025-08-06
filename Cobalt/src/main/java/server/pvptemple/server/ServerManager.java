package server.pvptemple.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
import server.pvptemple.CorePlugin;
import server.pvptemple.redis.JedisPublisher;
import server.pvptemple.redis.JedisSubscriber;
import server.pvptemple.server.ServerData;

public class ServerManager {
   @Getter
   private final CorePlugin plugin;
   @Getter
   private final Map<String, server.pvptemple.server.ServerData> servers;
   @Getter
   private JedisPublisher<JsonObject> serverHeartbeatPublisher;
   @Getter
   private JedisPublisher<JsonObject> proxyPublisher;
   @Getter
   private JedisSubscriber<String> serverHeartbeatSubscriber;
   @Getter
   @Setter
   private String serverName;
   @Getter
   @Setter
   private boolean joinable = false;
   @Setter @Getter
   private JsonObject extraData;

   public ServerManager(CorePlugin plugin) {
      this.plugin = plugin;
      this.servers = new ConcurrentHashMap<>();
      this.serverName = this.plugin.getConfig().getString("server-data.default-name");
      this.serverHeartbeatPublisher = new JedisPublisher<>(this.plugin.getJedisConfig().toJedisSettings(), "server_heartbeat");
      this.proxyPublisher = new JedisPublisher<>(this.plugin.getJedisConfig().toJedisSettings(), "proxy-core");
      this.serverHeartbeatSubscriber = new JedisSubscriber<>(this.plugin.getJedisConfig().toJedisSettings(), "server_heartbeat", String.class, (message) -> {
         try {
            JsonReader jsonReader = new JsonReader(new StringReader(message));
            jsonReader.setLenient(true);
            JsonObject jsonObject = (new JsonParser()).parse(jsonReader).getAsJsonObject();
            String serverName = jsonObject.get("server-name").getAsString();
            String action = jsonObject.get("action") != null ? jsonObject.get("action").getAsString() : null;
            if (action != null) {
               if (action.equals("online")) {
                  return;
               }

               if (action.equals("offline")) {
                  this.servers.remove(serverName);
                  return;
               }
            }

            try {
               server.pvptemple.server.ServerData serverData = (server.pvptemple.server.ServerData)this.servers.get(serverName);
               if (serverData == null) {
                  serverData = new server.pvptemple.server.ServerData();
                  this.servers.put(serverName, serverData);
               }

               int playersOnline = jsonObject.get("player-count").getAsInt();
               int maxPlayers = jsonObject.get("player-max").getAsInt();
               boolean whitelisted = jsonObject.get("whitelisted").getAsBoolean();
               boolean joinable = jsonObject.get("joinable").getAsBoolean();
               serverData.setServerName(serverName);
               serverData.setOnlinePlayers(playersOnline);
               serverData.setMaxPlayers(maxPlayers);
               serverData.setWhitelisted(whitelisted);
               serverData.setLastUpdate(System.currentTimeMillis());
               serverData.setJoinable(joinable);
               if (jsonObject.get("extra") != null) {
                  serverData.setExtra(jsonObject.get("extra").getAsJsonObject());
               }
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

      });
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("server-name", this.getServerName());
      jsonObject.addProperty("action", "online");
      this.serverHeartbeatPublisher.write(jsonObject);
   }

   public server.pvptemple.server.ServerData getServerDataByName(String name) {
      for(String serverKey : this.getServers().keySet()) {
         if (serverKey.equalsIgnoreCase(name)) {
            return (server.pvptemple.server.ServerData)this.getServers().get(serverKey);
         }

         if (((server.pvptemple.server.ServerData)this.getServers().get(serverKey)).getServerName().equalsIgnoreCase(name)) {
            return (server.pvptemple.server.ServerData)this.getServers().get(serverKey);
         }
      }

      return null;
   }

}
