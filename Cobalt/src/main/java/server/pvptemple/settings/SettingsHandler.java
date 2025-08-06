package server.pvptemple.settings;

import org.bukkit.entity.Player;
import server.pvptemple.inventory.InventoryUI;

public interface SettingsHandler {
   void onCreateSettings(InventoryUI var1, Player var2);
}
