package server.pvptemple.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

public class ItemBuilder implements Listener {
   private final ItemStack itemStack;

   public ItemBuilder(Material mat) {
      this.itemStack = new ItemStack(mat);
   }

   public ItemBuilder(ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   public ItemBuilder amount(int amount) {
      this.itemStack.setAmount(amount);
      return this;
   }

   public ItemBuilder name(String name) {
      ItemMeta meta = this.itemStack.getItemMeta();
      meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
      this.itemStack.setItemMeta(meta);
      return this;
   }

   public ItemBuilder lore(String name) {
      ItemMeta meta = this.itemStack.getItemMeta();
      List<String> lore = meta.getLore();
      if (lore == null) {
         lore = new ArrayList();
      }

      lore.add(ChatColor.translateAlternateColorCodes('&', name));
      meta.setLore(lore);
      this.itemStack.setItemMeta(meta);
      return this;
   }

   public ItemBuilder lore(List<String> lore) {
      List<String> toSet = new ArrayList();
      ItemMeta meta = this.itemStack.getItemMeta();

      for(String string : lore) {
         toSet.add(ChatColor.translateAlternateColorCodes('&', string));
      }

      meta.setLore(toSet);
      this.itemStack.setItemMeta(meta);
      return this;
   }

   public ItemBuilder durability(int durability) {
      this.itemStack.setDurability((short)durability);
      return this;
   }

   public ItemBuilder data(int data) {
      this.itemStack.setData(new MaterialData(this.itemStack.getType(), (byte)data));
      return this;
   }

   public ItemBuilder enchantment(Enchantment enchantment, int level) {
      this.itemStack.addUnsafeEnchantment(enchantment, level);
      return this;
   }

   public ItemBuilder enchantment(Enchantment enchantment) {
      this.itemStack.addUnsafeEnchantment(enchantment, 1);
      return this;
   }

   public ItemBuilder type(Material material) {
      this.itemStack.setType(material);
      return this;
   }

   public ItemBuilder clearLore() {
      ItemMeta meta = this.itemStack.getItemMeta();
      meta.setLore(new ArrayList());
      this.itemStack.setItemMeta(meta);
      return this;
   }

   public ItemBuilder clearEnchantments() {
      for(Enchantment e : this.itemStack.getEnchantments().keySet()) {
         this.itemStack.removeEnchantment(e);
      }

      return this;
   }

   public ItemBuilder color(Color color) {
      if (this.itemStack.getType() != Material.LEATHER_BOOTS && this.itemStack.getType() != Material.LEATHER_CHESTPLATE && this.itemStack.getType() != Material.LEATHER_HELMET && this.itemStack.getType() != Material.LEATHER_LEGGINGS) {
         throw new IllegalArgumentException("color() only applicable for leather armor!");
      } else {
         LeatherArmorMeta meta = (LeatherArmorMeta)this.itemStack.getItemMeta();
         meta.setColor(color);
         this.itemStack.setItemMeta(meta);
         return this;
      }
   }

   public ItemStack build() {
      return this.itemStack;
   }
}
