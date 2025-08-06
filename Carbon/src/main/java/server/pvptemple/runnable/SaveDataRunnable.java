package server.pvptemple.runnable;

import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;

public class SaveDataRunnable implements Runnable {
   private final Carbon plugin = Carbon.getInstance();

   public void run() {
      for(PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
         this.plugin.getPlayerManager().saveData(playerData);
      }

   }
}
