package server.pvptemple.api;

import server.pvptemple.CorePlugin;
import server.pvptemple.api.processor.AbstractRequestProcessor;

public class CoreProcessor extends AbstractRequestProcessor {
   private final CorePlugin plugin;

   public CoreProcessor(CorePlugin plugin, String apiUrl, String apiKey) {
      super(apiUrl, apiKey);
      this.plugin = plugin;
   }

   public boolean shouldSend() {
      return !this.plugin.getServer().isPrimaryThread();
   }

   public void runTask(Runnable runnable) {
      if (this.plugin.getServer().isPrimaryThread()) {
         runnable.run();
      } else {
         this.plugin.getServer().getScheduler().runTask(this.plugin, runnable);
      }

   }

   public void runTaskAsynchronously(Runnable runnable) {
      this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
   }
}
