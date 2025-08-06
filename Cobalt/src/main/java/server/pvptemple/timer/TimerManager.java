package server.pvptemple.timer;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import server.pvptemple.timer.Timer;

public class TimerManager implements Listener {
   private final Set<server.pvptemple.timer.Timer> timers = new LinkedHashSet();
   private final JavaPlugin plugin;

   public TimerManager(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public void registerTimer(server.pvptemple.timer.Timer timer) {
      this.timers.add(timer);
      if (timer instanceof Listener) {
         this.plugin.getServer().getPluginManager().registerEvents((Listener)timer, this.plugin);
      }

   }

   public void unregisterTimer(server.pvptemple.timer.Timer timer) {
      this.timers.remove(timer);
   }

   public <T extends server.pvptemple.timer.Timer> T getTimer(Class<T> timerClass) {
      for(server.pvptemple.timer.Timer timer : this.timers) {
         if (timer.getClass().equals(timerClass)) {
            return (T)timer;
         }
      }

      return null;
   }

   public JavaPlugin getPlugin() {
      return this.plugin;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TimerManager)) {
         return false;
      } else {
         TimerManager other = (TimerManager)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$timers = this.getTimers();
            Object other$timers = other.getTimers();
            if (this$timers == null) {
               if (other$timers != null) {
                  return false;
               }
            } else if (!this$timers.equals(other$timers)) {
               return false;
            }

            Object this$plugin = this.getPlugin();
            Object other$plugin = other.getPlugin();
            if (this$plugin == null) {
               if (other$plugin != null) {
                  return false;
               }
            } else if (!this$plugin.equals(other$plugin)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof TimerManager;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $timers = this.getTimers();
      result = result * 59 + ($timers == null ? 43 : $timers.hashCode());
      Object $plugin = this.getPlugin();
      result = result * 59 + ($plugin == null ? 43 : $plugin.hashCode());
      return result;
   }

   public String toString() {
      return "TimerManager(timers=" + this.getTimers() + ", plugin=" + this.getPlugin() + ")";
   }

   public Set<Timer> getTimers() {
      return this.timers;
   }
}
