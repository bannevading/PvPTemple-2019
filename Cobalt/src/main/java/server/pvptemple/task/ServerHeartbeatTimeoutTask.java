package server.pvptemple.task;

import java.beans.ConstructorProperties;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.server.ServerData;

public class ServerHeartbeatTimeoutTask extends BukkitRunnable {
   private static final long TIME_OUT_DELAY = 15000L;
   private final CorePlugin plugin;

   public void run() {
      for(String serverName : this.plugin.getServerManager().getServers().keySet()) {
         ServerData serverData = (ServerData)this.plugin.getServerManager().getServers().get(serverName);
         if (serverData != null && System.currentTimeMillis() - serverData.getLastUpdate() >= 15000L) {
            this.plugin.getServerManager().getServers().remove(serverName);
            this.plugin.getLogger().warning("The server \"" + serverName + "\" was removed due to it exceeding the timeout delay for heartbeats.");
         }
      }

   }

   @ConstructorProperties({"plugin"})
   public ServerHeartbeatTimeoutTask(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
