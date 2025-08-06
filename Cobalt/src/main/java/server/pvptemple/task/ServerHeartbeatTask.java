package server.pvptemple.task;

import com.google.gson.JsonObject;
import java.beans.ConstructorProperties;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;

public class ServerHeartbeatTask extends BukkitRunnable {
   private final CorePlugin plugin;

   public void run() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("server-name", this.plugin.getServerManager().getServerName());
      jsonObject.addProperty("player-count", (Number)this.plugin.getServer().getOnlinePlayers().size());
      jsonObject.addProperty("player-max", (Number)this.plugin.getServer().getMaxPlayers());
      jsonObject.addProperty("whitelisted", this.plugin.getServer().hasWhitelist());
      jsonObject.addProperty("joinable", this.plugin.getServerManager().isJoinable());
      if (this.plugin.getServerManager().getExtraData() != null) {
         jsonObject.add("extra", this.plugin.getServerManager().getExtraData());
      }

      this.plugin.getServerManager().getServerHeartbeatPublisher().write(jsonObject);
   }

   @ConstructorProperties({"plugin"})
   public ServerHeartbeatTask(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
