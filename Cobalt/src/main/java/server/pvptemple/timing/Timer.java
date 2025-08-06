package server.pvptemple.timing;

import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class Timer {
   @Getter
   private final Map<Long, Long> timings = new HashMap();
   @Getter
   private final String name;
   private boolean running;
   private long startTime;

   public void start() {
      if (this.running) {
         throw new IllegalStateException("Timer already running!");
      } else {
         this.running = true;
         this.startTime = System.nanoTime();
      }
   }

   public long stop() {
      if (!this.running) {
         throw new IllegalStateException("Timer is not running!");
      } else {
         this.running = false;
         long time = System.nanoTime() - this.startTime;
         this.timings.put(System.currentTimeMillis(), time);
         return time;
      }
   }

   @ConstructorProperties({"name"})
   public Timer(String name) {
      this.name = name;
   }

}
