package server.pvptemple.arena;

import java.beans.ConstructorProperties;
import server.pvptemple.util.CustomLocation;

public class StandaloneArena {
   private CustomLocation a;
   private CustomLocation b;
   private CustomLocation min;
   private CustomLocation max;
   private CustomLocation bedA;
   private CustomLocation bedB;

   public CustomLocation getA() {
      return this.a;
   }

   public CustomLocation getB() {
      return this.b;
   }

   public CustomLocation getMin() {
      return this.min;
   }

   public CustomLocation getMax() {
      return this.max;
   }

   public CustomLocation getBedA() {
      return this.bedA;
   }

   public CustomLocation getBedB() {
      return this.bedB;
   }

   public void setA(CustomLocation a) {
      this.a = a;
   }

   public void setB(CustomLocation b) {
      this.b = b;
   }

   public void setMin(CustomLocation min) {
      this.min = min;
   }

   public void setMax(CustomLocation max) {
      this.max = max;
   }

   public void setBedA(CustomLocation bedA) {
      this.bedA = bedA;
   }

   public void setBedB(CustomLocation bedB) {
      this.bedB = bedB;
   }

   @ConstructorProperties({"a", "b", "min", "max", "bedA", "bedB"})
   public StandaloneArena(CustomLocation a, CustomLocation b, CustomLocation min, CustomLocation max, CustomLocation bedA, CustomLocation bedB) {
      this.a = a;
      this.b = b;
      this.min = min;
      this.max = max;
      this.bedA = bedA;
      this.bedB = bedB;
   }

   public StandaloneArena() {
   }
}
