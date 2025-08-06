package server.pvptemple.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import server.pvptemple.Carbon;
import server.pvptemple.match.Match;
import server.pvptemple.player.PlayerData;
import server.pvptemple.util.MathUtil;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.ItemUtil;
import server.pvptemple.util.finalutil.StringUtil;

public class InventorySnapshot {
   private final InventoryUI inventoryUI;
   private final ItemStack[] originalInventory;
   private final ItemStack[] originalArmor;
   private final UUID snapshotId = UUID.randomUUID();

   public InventorySnapshot(final Player player, Match match) {
      ItemStack[] contents = player.getInventory().getContents();
      ItemStack[] armor = player.getInventory().getArmorContents();
      this.originalInventory = contents;
      this.originalArmor = armor;
      PlayerData playerData = Carbon.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
      double health = player.getHealth();
      double food = (double)player.getFoodLevel();
      List<String> potionEffectStrings = new ArrayList();

      for(PotionEffect potionEffect : player.getActivePotionEffects()) {
         String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
         String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
         String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());
         potionEffectStrings.add(CC.WHITE + effectName + " " + romanNumeral + CC.GRAY + " (" + duration + ")");
      }

      this.inventoryUI = new InventoryUI(player.getName(), 6);

      for(int i = 0; i < 9; ++i) {
         this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
         this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
         this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
         this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
      }

      boolean potionMatch = false;
      boolean soupMatch = false;

      for(ItemStack item : match.getKit().getContents()) {
         if (item != null) {
            if (item.getType() == Material.MUSHROOM_SOUP) {
               soupMatch = true;
               break;
            }

            if (item.getType() == Material.POTION && item.getDurability() == 16421) {
               potionMatch = true;
               break;
            }
         }
      }

      if (potionMatch) {
         int potCount = (int)Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter((d) -> d == 16421).count();
         this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.POTION, CC.YELLOW + "Health Potions: " + CC.WHITE + potCount, potCount, (short)16421), new String[]{CC.YELLOW + "Missed Potions: " + CC.WHITE + playerData.getMissedPots()})));
      } else if (soupMatch) {
         int soupCount = (int)Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter((d) -> d == Material.MUSHROOM_SOUP).count();
         this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.MUSHROOM_SOUP, CC.YELLOW + "Remaining Soups: " + CC.WHITE + soupCount, soupCount, (short)16421)));
      }

      this.inventoryUI.setItem(48, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, CC.YELLOW + "Hearts: " + CC.WHITE + MathUtil.roundToHalves(health / (double)2.0F) + " / 10 ❤", (int)Math.round(health / (double)2.0F))));
      this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, CC.YELLOW + "Hunger: " + CC.WHITE + MathUtil.roundToHalves(food / (double)2.0F) + " / 10 ❤", (int)Math.round(food / (double)2.0F))));
      this.inventoryUI.setItem(50, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.BREWING_STAND_ITEM, CC.YELLOW + "Potion Effects", potionEffectStrings.size()), (String[])potionEffectStrings.toArray(new String[0]))));
      this.inventoryUI.setItem(51, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.DIAMOND_SWORD, CC.YELLOW + "Statistics"), new String[]{CC.GRAY + "Longest Combo: " + CC.WHITE + playerData.getLongestCombo() + " Hit" + (playerData.getLongestCombo() > 1 ? "s" : ""), CC.GRAY + "Total Hits: " + CC.WHITE + playerData.getHits() + " Hit" + (playerData.getHits() > 1 ? "s" : "")})));
      if (!match.isParty()) {
         for(int i = 0; i < 2; ++i) {
            this.inventoryUI.setItem(i == 0 ? 53 : 45, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.PAPER, CC.YELLOW + "View Other Inventory"), new String[]{CC.GRAY + "Click to view the other inventory"})) {
               public void onClick(InventoryClickEvent inventoryClickEvent) {
                  Player clicker = (Player)inventoryClickEvent.getWhoClicked();
                  if (Carbon.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
                     clicker.closeInventory();
                     Carbon.getInstance().getServer().dispatchCommand(clicker, "inv " + Carbon.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
                  }

               }
            });
         }
      }

      for(int i = 36; i < 40; ++i) {
         this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
      }

   }

   public JSONObject toJson() {
      JSONObject object = new JSONObject();
      JSONObject inventoryObject = new JSONObject();

      for(int i = 0; i < this.originalInventory.length; ++i) {
         inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
      }

      object.put("inventory", inventoryObject);
      JSONObject armourObject = new JSONObject();

      for(int i = 0; i < this.originalArmor.length; ++i) {
         armourObject.put(i, this.encodeItem(this.originalArmor[i]));
      }

      object.put("armour", armourObject);
      return object;
   }

   private JSONObject encodeItem(ItemStack itemStack) {
      if (itemStack != null && itemStack.getType() != Material.AIR) {
         JSONObject object = new JSONObject();
         object.put("material", itemStack.getType().name());
         object.put("durability", itemStack.getDurability());
         object.put("amount", itemStack.getAmount());
         JSONObject enchants = new JSONObject();

         for(Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
         }

         object.put("enchants", enchants);
         return object;
      } else {
         return null;
      }
   }

   public InventoryUI getInventoryUI() {
      return this.inventoryUI;
   }

   public ItemStack[] getOriginalInventory() {
      return this.originalInventory;
   }

   public ItemStack[] getOriginalArmor() {
      return this.originalArmor;
   }

   public UUID getSnapshotId() {
      return this.snapshotId;
   }
}
