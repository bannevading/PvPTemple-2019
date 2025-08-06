package server.pvptemple.queue;

import java.beans.ConstructorProperties;

public enum QueueType {
   UNRANKED("Unranked"),
   RANKED("Ranked"),
   PREMIUM("Premium");

   private final String name;

   public boolean isRanked() {
      return this != UNRANKED;
   }

   public String getName() {
      return this.name;
   }

   @ConstructorProperties({"name"})
   private QueueType(String name) {
      this.name = name;
   }
}
