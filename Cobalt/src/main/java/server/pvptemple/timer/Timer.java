package server.pvptemple.timer;

import lombok.Getter;
import java.beans.ConstructorProperties;

public abstract class Timer {
   @Getter protected final String name;
   @Getter protected final long defaultCooldown;

   public final String getDisplayName() {
      return this.name;
   }

   @ConstructorProperties({"name", "defaultCooldown"})
   public Timer(String name, long defaultCooldown) {
      this.name = name;
      this.defaultCooldown = defaultCooldown;
   }

}
