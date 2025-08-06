package server.pvptemple.task;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.event.PreShutdownEvent;
import server.pvptemple.util.finalutil.BungeeUtil;
import server.pvptemple.util.finalutil.CC;

public class ShutdownTask extends BukkitRunnable {
   private static final List<Integer> BROADCAST_TIMES = Arrays.asList(3600, 1800, 900, 600, 300, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);
   private CorePlugin plugin;
   private int secondsUntilShutdown;

   public void run() {
      if (BROADCAST_TIMES.contains(this.secondsUntilShutdown)) {
         this.plugin.getServer().broadcastMessage(CC.PRIMARY + "The server will shutdown in " + CC.SECONDARY + this.secondsUntilShutdown + CC.PRIMARY + " seconds.");
      }

      if (this.secondsUntilShutdown <= 5) {
         this.plugin.getServer().getOnlinePlayers().forEach((player) -> BungeeUtil.sendToServer(player, "lobby-01"));
      }

      if (this.secondsUntilShutdown <= 0) {
         PreShutdownEvent event = new PreShutdownEvent();
         this.plugin.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return;
         }

         this.plugin.getServer().getOnlinePlayers().forEach((player) -> player.sendMessage(CC.RED + "The server has shut down."));
         this.plugin.getServer().shutdown();
      }

      --this.secondsUntilShutdown;
   }

   public CorePlugin getPlugin() {
      return this.plugin;
   }

   public int getSecondsUntilShutdown() {
      return this.secondsUntilShutdown;
   }

   public void setPlugin(CorePlugin plugin) {
      this.plugin = plugin;
   }

   public void setSecondsUntilShutdown(int secondsUntilShutdown) {
      this.secondsUntilShutdown = secondsUntilShutdown;
   }

   @ConstructorProperties({"plugin", "secondsUntilShutdown"})
   public ShutdownTask(CorePlugin plugin, int secondsUntilShutdown) {
      this.plugin = plugin;
      this.secondsUntilShutdown = secondsUntilShutdown;
   }
}
