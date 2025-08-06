package server.pvptemple.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionEffect;
import server.pvptemple.CorePlugin;
import server.pvptemple.gson.item.EnchantType;
import server.pvptemple.gson.item.ItemMetaType;
import server.pvptemple.gson.item.ItemType;

public class ItemStackTypeAdapterFactory implements TypeAdapterFactory {
   public static ItemType serializeItem(ItemStack itemStack) {
      Preconditions.checkNotNull(itemStack);
      ItemMetaType itemMeta = null;
      List<EnchantType> itemEnchants = null;
      if (itemStack.hasItemMeta()) {
         ItemMeta itemStackMeta = itemStack.getItemMeta();
         itemMeta = new ItemMetaType();
         if (itemStackMeta.hasDisplayName()) {
            itemMeta.setDisplayName(itemStackMeta.getDisplayName());
         }

         if (itemStackMeta.hasLore()) {
            itemMeta.setLore(itemStackMeta.getLore());
         }

         if (itemStackMeta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemStackMeta;
            if (esm.hasStoredEnchants()) {
               List<EnchantType> storedEnchants = new ArrayList<>();

               for(Map.Entry<Enchantment, Integer> entry : esm.getStoredEnchants().entrySet()) {
                  storedEnchants.add(new EnchantType(((Enchantment)entry.getKey()).getName(), (Integer)entry.getValue()));
               }

               itemMeta.setStoredEnchants(storedEnchants);
            }
         }

         if (itemStackMeta instanceof Repairable) {
            itemMeta.setRepairCost(((Repairable)itemStackMeta).getRepairCost());
         }

         if (itemStackMeta instanceof LeatherArmorMeta) {
            itemMeta.setLeatherArmorColor(((LeatherArmorMeta)itemStackMeta).getColor().asRGB());
         }

         if (itemStackMeta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta)itemStackMeta;
            if (potionMeta.hasCustomEffects()) {
               List<PotionEffect> potionEffects = new ArrayList<>();
               potionEffects.addAll(potionMeta.getCustomEffects());
               itemMeta.setPotionEffects(potionEffects);
            }
         }
      }

      for(Map.Entry<Enchantment, Integer> e : itemStack.getEnchantments().entrySet()) {
         if (itemEnchants == null) {
            itemEnchants = new ArrayList<>();
         }

         itemEnchants.add(new EnchantType(e.getKey().getName(), e.getValue()));
      }

      ItemType item = new ItemType(itemStack.getType().toString(), itemStack.getDurability(), itemStack.getAmount(), itemEnchants, itemMeta);
      return item;
   }

   public static ItemStack deserializeItem(ItemType item) {
      Preconditions.checkNotNull(item);
      ItemStack itemStack = new ItemStack(Material.matchMaterial(item.getType()), item.getAmount());
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemStack.setDurability(item.getDurability());
      if (item.getEnchants() != null) {
         for(EnchantType enchant : item.getEnchants()) {
            itemMeta.addEnchant(Enchantment.getByName(enchant.getType()), enchant.getTier(), true);
         }
      }

      if (item.getMeta() != null) {
         if (item.getMeta().getDisplayName() != null) {
            itemMeta.setDisplayName(item.getMeta().getDisplayName());
         }

         if (item.getMeta().getLore() != null) {
            itemMeta.setLore(item.getMeta().getLore());
         }

         if (itemMeta instanceof EnchantmentStorageMeta && item.getMeta().getStoredEnchants() != null) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemMeta;

            for(EnchantType enchant : item.getMeta().getStoredEnchants()) {
               esm.addStoredEnchant(Enchantment.getByName(enchant.getType()), enchant.getTier(), true);
            }
         }

         if (itemMeta instanceof Repairable && item.getMeta().getRepairCost() != null) {
            ((Repairable)itemMeta).setRepairCost(item.getMeta().getRepairCost());
         }

         if (itemMeta instanceof LeatherArmorMeta && item.getMeta().getLeatherArmorColor() != null) {
            ((LeatherArmorMeta)itemMeta).setColor(Color.fromRGB(item.getMeta().getLeatherArmorColor()));
         }

         if (itemMeta instanceof PotionMeta && item.getMeta().getPotionEffects() != null) {
            PotionMeta potionMeta = (PotionMeta)itemMeta;

            for(PotionEffect effect : item.getMeta().getPotionEffects()) {
               potionMeta.addCustomEffect(effect, true);
            }
         }
      }

      itemStack.setItemMeta(itemMeta);
      return itemStack;
   }

   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      return !ItemStack.class.isAssignableFrom(type.getRawType()) ? null : new TypeAdapter<T>() {
         public void write(JsonWriter writer, T item) throws IOException {
            if (item == null) {
               writer.nullValue();
            } else {
               CorePlugin.GSON.toJson(ItemStackTypeAdapterFactory.serializeItem((ItemStack)item), ItemType.class, (JsonWriter)writer);
            }

         }

         public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
               reader.nextNull();
               return null;
            } else {
               return (T)ItemStackTypeAdapterFactory.deserializeItem((ItemType)CorePlugin.GSON.fromJson((JsonReader)reader, ItemType.class));
            }
         }
      };
   }
}
