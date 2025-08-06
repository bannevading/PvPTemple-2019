package server.pvptemple.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import server.pvptemple.CorePlugin;
import server.pvptemple.event.player.RankChangeEvent;

public class RankListener implements Listener {
   @EventHandler
   public void onRankChange(RankChangeEvent e) {
      if (!CorePlugin.getInstance().getDisguiseManager().isDisguised(e.getUniqueId())) {
         e.getMineman().updateTabList(e.getTo());
      }
   }
}
