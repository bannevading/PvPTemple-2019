package server.pvptemple.timer;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import server.pvptemple.CorePlugin;
import server.pvptemple.timer.event.TimerExpireEvent;

public class TimerCooldown {
   @Getter
   private final server.pvptemple.timer.Timer timer;
   private final UUID owner;
   private BukkitTask eventNotificationTask;
   @Getter
   private long expiryMillis;
   @Setter @Getter private long pauseMillis;

   protected TimerCooldown(server.pvptemple.timer.Timer timer, long duration) {
      this.owner = null;
      this.timer = timer;
      this.setRemaining(duration);
   }

   protected TimerCooldown(server.pvptemple.timer.Timer timer, UUID playerUUID, long duration) {
      this.timer = timer;
      this.owner = playerUUID;
      this.setRemaining(duration);
   }

   public long getRemaining() {
      return this.getRemaining(false);
   }

   protected void setRemaining(long milliseconds) throws IllegalStateException {
      if (milliseconds <= 0L) {
         this.cancel();
      } else {
         long expiryMillis = System.currentTimeMillis() + milliseconds;
         if (expiryMillis != this.expiryMillis) {
            this.expiryMillis = expiryMillis;
            if (this.eventNotificationTask != null) {
               this.eventNotificationTask.cancel();
            }

            long ticks = milliseconds / 50L;
            this.eventNotificationTask = (new BukkitRunnable() {
               public void run() {
                  if (TimerCooldown.this.timer instanceof server.pvptemple.timer.PlayerTimer && TimerCooldown.this.owner != null) {
                     ((PlayerTimer)TimerCooldown.this.timer).handleExpiry(CorePlugin.getInstance().getServer().getPlayer(TimerCooldown.this.owner), TimerCooldown.this.owner);
                  }

                  CorePlugin.getInstance().getServer().getPluginManager().callEvent(new TimerExpireEvent(TimerCooldown.this.owner, TimerCooldown.this.timer));
               }
            }).runTaskLater(JavaPlugin.getProvidingPlugin(this.getClass()), ticks);
         }

      }
   }

   protected long getRemaining(boolean ignorePaused) {
      return !ignorePaused && this.pauseMillis != 0L ? this.pauseMillis : this.expiryMillis - System.currentTimeMillis();
   }

   protected boolean isPaused() {
      return this.pauseMillis != 0L;
   }

   public void setPaused(boolean paused) {
      if (paused != this.isPaused()) {
         if (paused) {
            this.pauseMillis = this.getRemaining(true);
            this.cancel();
         } else {
            this.setRemaining(this.pauseMillis);
            this.pauseMillis = 0L;
         }
      }

   }

   protected void cancel() throws IllegalStateException {
      if (this.eventNotificationTask != null) {
         this.eventNotificationTask.cancel();
         this.eventNotificationTask = null;
      }

   }

}
