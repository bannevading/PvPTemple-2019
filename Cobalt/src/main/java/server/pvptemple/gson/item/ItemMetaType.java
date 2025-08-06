package server.pvptemple.gson.item;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;
import server.pvptemple.gson.item.EnchantType;

public class ItemMetaType {
   @Getter @Setter
   private String displayName;
   @Setter
   @Getter
   private List<String> lore;
   @Setter
   @Getter
   private List<server.pvptemple.gson.item.EnchantType> storedEnchants;
   @Setter
   @Getter
   private Integer repairCost;
   @Setter
   @Getter
   private Integer leatherArmorColor;
   @Setter
   @Getter
   private List<PotionEffect> potionEffects;

}
