package server.pvptemple.timing;

import server.pvptemple.timing.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimingManager {
   public static final TimingManager INSTANCE = new TimingManager();
   private final Map<String, server.pvptemple.timing.Timer> timerMap = new ConcurrentHashMap();

   public server.pvptemple.timing.Timer getTimer(String name) {
      return (server.pvptemple.timing.Timer)this.timerMap.computeIfAbsent(name, (s) -> (server.pvptemple.timing.Timer)this.timerMap.put(s, new Timer(s)));
   }

   public void start(String name) {
      this.getTimer(name).start();
   }

   public long stop(String name) {
      return this.getTimer(name).stop();
   }
}
