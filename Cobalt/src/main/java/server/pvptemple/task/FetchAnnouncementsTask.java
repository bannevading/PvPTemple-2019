package server.pvptemple.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.AnnouncementRequest;

public class FetchAnnouncementsTask extends BukkitRunnable {
   private final CorePlugin plugin;

   private boolean checkServer(String serverName) {
      return serverName.equals("*") || serverName.equals(this.plugin.getServerManager().getServerName()) || this.plugin.getServerManager().getServerName().matches(serverName);
   }

   public void run() {
      this.plugin.getRequestProcessor().sendRequestAsync(new AnnouncementRequest(this.plugin.getServerManager().getServerName()), (element) -> {
         JsonArray announcements = element.getAsJsonArray();
         List<String> newAnnouncements = new ArrayList();

         for(int index = 0; index < announcements.size(); ++index) {
            JsonObject announcement = announcements.get(index).getAsJsonObject();
            String serverName = announcement.get("server").getAsString();
            if (this.checkServer(serverName)) {
               String message = announcement.get("announcement").getAsString();
               newAnnouncements.add(ChatColor.translateAlternateColorCodes('&', message));
            }
         }

         this.plugin.getAnnouncements().clear();
         this.plugin.getAnnouncements().addAll(newAnnouncements);
      });
   }

   @ConstructorProperties({"plugin"})
   public FetchAnnouncementsTask(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
