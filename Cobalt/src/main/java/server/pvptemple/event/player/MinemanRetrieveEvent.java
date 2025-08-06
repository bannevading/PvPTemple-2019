package server.pvptemple.event.player;

import server.pvptemple.event.MinemanEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.util.BanWrapper;

public class MinemanRetrieveEvent extends MinemanEvent {
   private final BanWrapper banWrapper;

   public MinemanRetrieveEvent(Mineman mineman, BanWrapper banWrapper) {
      super(mineman);
      this.banWrapper = banWrapper;
   }

   public BanWrapper getBanWrapper() {
      return this.banWrapper;
   }
}
