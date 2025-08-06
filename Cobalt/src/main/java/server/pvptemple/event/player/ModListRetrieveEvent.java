package server.pvptemple.event.player;

import java.util.Map;
import org.bukkit.entity.Player;
import server.pvptemple.event.PlayerEvent;

public class ModListRetrieveEvent extends PlayerEvent {
   private final Map<String, String> mods;

   public ModListRetrieveEvent(Player player, Map<String, String> mods) {
      super(player);
      this.mods = mods;
   }

   public Map<String, String> getMods() {
      return this.mods;
   }
}
