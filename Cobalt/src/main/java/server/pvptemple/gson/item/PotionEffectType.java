package server.pvptemple.gson.item;

import lombok.Getter;

import java.beans.ConstructorProperties;

public class PotionEffectType {
   @Getter
   private final String type;
   @Getter
   private final int duration;
   @Getter
   private final int amplifier;

   @ConstructorProperties({"type", "duration", "amplifier"})
   public PotionEffectType(String type, int duration, int amplifier) {
      this.type = type;
      this.duration = duration;
      this.amplifier = amplifier;
   }
}
