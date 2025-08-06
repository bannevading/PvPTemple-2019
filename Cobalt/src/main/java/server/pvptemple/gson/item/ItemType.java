package server.pvptemple.gson.item;

import lombok.Getter;
import server.pvptemple.gson.item.EnchantType;
import server.pvptemple.gson.item.ItemMetaType;

import java.beans.ConstructorProperties;
import java.util.List;

public class ItemType {
   @Getter
   private final String type;
   @Getter
   private final short durability;
   @Getter
   private final int amount;
   @Getter
   private final List<EnchantType> enchants;
   @Getter
   private final ItemMetaType meta;

    @ConstructorProperties({"type", "durability", "amount", "enchants", "meta"})
   public ItemType(String type, short durability, int amount, List<EnchantType> enchants, ItemMetaType meta) {
      this.type = type;
      this.durability = durability;
      this.amount = amount;
      this.enchants = enchants;
      this.meta = meta;
   }
}
