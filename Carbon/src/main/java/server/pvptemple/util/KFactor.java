package server.pvptemple.util;

import java.beans.ConstructorProperties;

public class KFactor {
   private final int startIndex;
   private final int endIndex;
   private final double value;

   public int getStartIndex() {
      return this.startIndex;
   }

   public int getEndIndex() {
      return this.endIndex;
   }

   public double getValue() {
      return this.value;
   }

   @ConstructorProperties({"startIndex", "endIndex", "value"})
   public KFactor(int startIndex, int endIndex, double value) {
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.value = value;
   }
}
