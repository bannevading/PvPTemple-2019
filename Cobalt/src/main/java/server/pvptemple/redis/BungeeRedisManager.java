package server.pvptemple.redis;

import com.google.gson.JsonObject;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.entity.wrapper.DummyWrapper;
import server.pvptemple.redis.MessageType;
import server.pvptemple.redis.subscription.JedisSubscriptionHandler;

public class BungeeRedisManager implements JedisSubscriptionHandler<JsonObject> {
   public void handleMessage(JsonObject object) {
      try {
         if (object.has("type")) {
            server.pvptemple.redis.MessageType type = server.pvptemple.redis.MessageType.valueOf(object.get("type").getAsString());
            Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
               UUID uuid = UUID.fromString(object.get("uuid").getAsString());
               String server;
               switch (type) {
                  case ADD:
                     server = object.get("server").getAsString();
                     if (CorePlugin.getInstance().getServerManager().getServerName().equals(server)) {
                        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
                           JsonObject newObject = new JsonObject();
                           newObject.addProperty("type", MessageType.FORCE_REMOVE.toString());
                           newObject.addProperty("uuid", uuid.toString());
                           CorePlugin.getInstance().getServerManager().getProxyPublisher().write(object);
                        } else {
                           CorePlugin.getInstance().getPlayerManager().getDummyPlayers().add(uuid);
                           DummyWrapper wrapper = new DummyWrapper(Bukkit.getWorld("world"), uuid);
                           wrapper.spawn();
                        }
                     }
                     break;
                  case REMOVE:
                     server = object.get("server").getAsString();
                     if (CorePlugin.getInstance().getServerManager().getServerName().equalsIgnoreCase(server)) {
                        Player player = Bukkit.getPlayer(uuid);
                        CorePlugin.getInstance().getPlayerManager().getDummyPlayers().remove(uuid);
                        if (player != null) {
                           Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                              PlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
                              playerList.disconnect(((CraftPlayer)player).getHandle());
                           });
                        }
                     }
               }

            });
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}
