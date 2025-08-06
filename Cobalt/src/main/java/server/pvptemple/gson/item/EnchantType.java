package server.pvptemple.gson.item;

import lombok.Getter;

import java.beans.ConstructorProperties;

public class EnchantType {
   @Getter
   private final String type;
   @Getter
   private final int tier;

   @ConstructorProperties({"type", "tier"})
   public EnchantType(String type, int tier) {
      this.type = type;
      this.tier = tier;
   }
}
