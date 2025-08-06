package server.pvptemple.queue;

import java.beans.ConstructorProperties;

public class QueueEntry {
   private final QueueType queueType;
   private final String kitName;
   private final int elo;
   private final boolean party;

   public QueueType getQueueType() {
      return this.queueType;
   }

   public String getKitName() {
      return this.kitName;
   }

   public int getElo() {
      return this.elo;
   }

   public boolean isParty() {
      return this.party;
   }

   @ConstructorProperties({"queueType", "kitName", "elo", "party"})
   public QueueEntry(QueueType queueType, String kitName, int elo, boolean party) {
      this.queueType = queueType;
      this.kitName = kitName;
      this.elo = elo;
      this.party = party;
   }
}
