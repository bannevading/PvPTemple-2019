package server.pvptemple.settings;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;
import server.pvptemple.CorePlugin;
import server.pvptemple.settings.SettingsHandler;

public class SettingsManager {
   private final Set<server.pvptemple.settings.SettingsHandler> settingsHandlers = new HashSet();
   private final CorePlugin plugin;

   public void addSettingsHandler(server.pvptemple.settings.SettingsHandler settingsHandler) {
      this.settingsHandlers.add(settingsHandler);
   }

   public Set<SettingsHandler> getSettingsHandlers() {
      return this.settingsHandlers;
   }

   public CorePlugin getPlugin() {
      return this.plugin;
   }

   @ConstructorProperties({"plugin"})
   public SettingsManager(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
