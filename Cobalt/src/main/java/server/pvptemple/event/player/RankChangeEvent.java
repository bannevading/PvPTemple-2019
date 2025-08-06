package server.pvptemple.event.player;

import server.pvptemple.event.MinemanPlayerEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;

public class RankChangeEvent extends MinemanPlayerEvent {
   private Rank from;
   private Rank to;

   public RankChangeEvent(Mineman mineman, Rank from, Rank to) {
      super(mineman);
      this.from = from;
      this.to = to;
   }

   public Rank getFrom() {
      return this.from;
   }

   public Rank getTo() {
      return this.to;
   }
}
