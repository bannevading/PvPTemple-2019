package server.pvptemple.arena;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import server.pvptemple.util.CustomLocation;

public class Arena {
   private final String name;
   private List<StandaloneArena> standaloneArenas = new ArrayList();
   private List<StandaloneArena> availableArenas = new ArrayList();
   private CustomLocation a;
   private CustomLocation b;
   private CustomLocation min;
   private CustomLocation max;
   private CustomLocation bedA;
   private CustomLocation bedB;
   private boolean enabled;

   public StandaloneArena getAvailableArena() {
      StandaloneArena arena = (StandaloneArena)this.availableArenas.get(0);
      this.availableArenas.remove(0);
      return arena;
   }

   public void addStandaloneArena(StandaloneArena arena) {
      this.standaloneArenas.add(arena);
   }

   public void addAvailableArena(StandaloneArena arena) {
      this.availableArenas.add(arena);
   }

   public String getName() {
      return this.name;
   }

   public List<StandaloneArena> getStandaloneArenas() {
      return this.standaloneArenas;
   }

   public List<StandaloneArena> getAvailableArenas() {
      return this.availableArenas;
   }

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

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setStandaloneArenas(List<StandaloneArena> standaloneArenas) {
      this.standaloneArenas = standaloneArenas;
   }

   public void setAvailableArenas(List<StandaloneArena> availableArenas) {
      this.availableArenas = availableArenas;
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

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @ConstructorProperties({"name", "standaloneArenas", "availableArenas", "a", "b", "min", "max", "bedA", "bedB", "enabled"})
   public Arena(String name, List<StandaloneArena> standaloneArenas, List<StandaloneArena> availableArenas, CustomLocation a, CustomLocation b, CustomLocation min, CustomLocation max, CustomLocation bedA, CustomLocation bedB, boolean enabled) {
      this.name = name;
      this.standaloneArenas = standaloneArenas;
      this.availableArenas = availableArenas;
      this.a = a;
      this.b = b;
      this.min = min;
      this.max = max;
      this.bedA = bedA;
      this.bedB = bedB;
      this.enabled = enabled;
   }

   @ConstructorProperties({"name"})
   public Arena(String name) {
      this.name = name;
   }
}
