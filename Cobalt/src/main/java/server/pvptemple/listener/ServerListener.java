package server.pvptemple.listener;

import java.beans.ConstructorProperties;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import server.pvptemple.CorePlugin;
import server.pvptemple.event.PreShutdownEvent;

public class ServerListener implements Listener {
   private final CorePlugin plugin;

   @EventHandler
   public void onServerCommand(ServerCommandEvent event) {
      String command = event.getCommand().replace("/", "");
      if (command != null) {
         if (command.split(" ")[0].equalsIgnoreCase("stop")) {
            PreShutdownEvent shutdownEvent = new PreShutdownEvent();
            this.plugin.getServer().getPluginManager().callEvent(shutdownEvent);
            if (shutdownEvent.isCancelled()) {
               return;
            }

            this.handleShutdown();
         }

      }
   }

   @EventHandler
   public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
      if (event.getPlayer().hasPermission("bukkit.command.stop") && event.getMessage().replace("/", "").split(" ")[0].equalsIgnoreCase("stop")) {
         PreShutdownEvent shutdownEvent = new PreShutdownEvent();
         this.plugin.getServer().getPluginManager().callEvent(shutdownEvent);
         if (shutdownEvent.isCancelled()) {
            return;
         }

         this.handleShutdown();
      }

   }

   private void handleShutdown() {
      try {
         Thread.sleep(500L);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

   }

   @ConstructorProperties({"plugin"})
   public ServerListener(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
