package server.pvptemple.api.abstr;

import org.bukkit.Bukkit;
import server.pvptemple.api.callback.Callback;
import server.pvptemple.api.callback.ErrorCallback;

public abstract class AbstractBukkitCallback implements Callback, ErrorCallback {
   public void onError(String message) {
      Bukkit.getLogger().severe("[WEBAPI]: " + message);
   }
}
