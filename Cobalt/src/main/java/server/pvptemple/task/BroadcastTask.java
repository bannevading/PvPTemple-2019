package server.pvptemple.task;

import java.beans.ConstructorProperties;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;

public class BroadcastTask extends BukkitRunnable {
   private final CorePlugin plugin;
   private int index = -1;

   public void run() {
      if (this.plugin.getAnnouncements().size() != 0) {
         if (++this.index >= this.plugin.getAnnouncements().size()) {
            this.index = 0;
         }

         String broadcastMessage = (String)this.plugin.getAnnouncements().get(this.index);
         this.plugin.getServer().broadcastMessage(broadcastMessage);
      }
   }

   @ConstructorProperties({"plugin"})
   public BroadcastTask(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
