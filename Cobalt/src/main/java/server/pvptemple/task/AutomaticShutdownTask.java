package server.pvptemple.task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;

public class AutomaticShutdownTask extends BukkitRunnable {
   private DateFormat format = new SimpleDateFormat("HH:mm:ss");

   public void run() {
      Date now = new Date();

      try {
         if (this.format.format(now).equals(this.format.format(this.format.parse("04:00:00").getTime() - 600000L))) {
            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shutdown time 600"));
         }
      } catch (ParseException e) {
         e.printStackTrace();
      }

   }
}
