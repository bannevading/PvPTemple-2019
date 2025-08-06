package server.pvptemple.util.tab;

import org.bukkit.Bukkit;
import server.pvptemple.Carbon;

final class TabThread extends Thread {
   TabThread() {
      super("Tab Thread");
      this.setDaemon(false);
   }

   public void run() {
      while(true) {
         Bukkit.getOnlinePlayers().stream().filter((player) -> Carbon.getInstance().getPlayerManager().getPlayerData(player.getUniqueId()).isTab()).forEach((player) -> {
            try {
               TabManager.updatePlayer(player);
            } catch (Exception e) {
               e.printStackTrace();
            }

         });

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
}
