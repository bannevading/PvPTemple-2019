package server.pvptemple.kit;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.util.ItemBuilder;
import server.pvptemple.util.finalutil.CC;

public class Kit {
   private final String name;
   private ItemStack[] contents = new ItemStack[36];
   private ItemStack[] armor = new ItemStack[4];
   private ItemStack[] kitEditContents = new ItemStack[36];
   private ItemStack icon;
   private List<String> excludedArenas = new ArrayList();
   private List<String> arenaWhiteList = new ArrayList();
   private boolean enabled;
   private boolean ranked;
   private boolean combo;
   private boolean sumo;
   private boolean build;
   private boolean spleef;
   private boolean bedwars;

   public void applyToPlayer(Player player) {
      player.getInventory().setContents(this.contents);
      player.getInventory().setArmorContents(this.armor);
      player.updateInventory();
      player.sendMessage(CC.GREEN + "Giving you the default loadout.");
   }

   public void whitelistArena(String arena) {
      if (!this.arenaWhiteList.remove(arena)) {
         this.arenaWhiteList.add(arena);
      }

   }

   public void excludeArena(String arena) {
      if (!this.excludedArenas.remove(arena)) {
         this.excludedArenas.add(arena);
      }

   }

   public ItemStack getEditorStack() {
      return (new ItemBuilder(this.icon.getType())).durability(this.icon.getDurability()).name("&a" + this.name).lore(Arrays.asList("", CC.GRAY + "Click to edit the", CC.GRAY + "layout of " + CC.RESET + this.name + CC.GRAY + " kit.", "", CC.GREEN + "» Click to edit «")).build();
   }

   public String getName() {
      return this.name;
   }

   public ItemStack[] getContents() {
      return this.contents;
   }

   public ItemStack[] getArmor() {
      return this.armor;
   }

   public ItemStack[] getKitEditContents() {
      return this.kitEditContents;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public List<String> getExcludedArenas() {
      return this.excludedArenas;
   }

   public List<String> getArenaWhiteList() {
      return this.arenaWhiteList;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isRanked() {
      return this.ranked;
   }

   public boolean isCombo() {
      return this.combo;
   }

   public boolean isSumo() {
      return this.sumo;
   }

   public boolean isBuild() {
      return this.build;
   }

   public boolean isSpleef() {
      return this.spleef;
   }

   public boolean isBedwars() {
      return this.bedwars;
   }

   public void setContents(ItemStack[] contents) {
      this.contents = contents;
   }

   public void setArmor(ItemStack[] armor) {
      this.armor = armor;
   }

   public void setKitEditContents(ItemStack[] kitEditContents) {
      this.kitEditContents = kitEditContents;
   }

   public void setIcon(ItemStack icon) {
      this.icon = icon;
   }

   public void setExcludedArenas(List<String> excludedArenas) {
      this.excludedArenas = excludedArenas;
   }

   public void setArenaWhiteList(List<String> arenaWhiteList) {
      this.arenaWhiteList = arenaWhiteList;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void setRanked(boolean ranked) {
      this.ranked = ranked;
   }

   public void setCombo(boolean combo) {
      this.combo = combo;
   }

   public void setSumo(boolean sumo) {
      this.sumo = sumo;
   }

   public void setBuild(boolean build) {
      this.build = build;
   }

   public void setSpleef(boolean spleef) {
      this.spleef = spleef;
   }

   public void setBedwars(boolean bedwars) {
      this.bedwars = bedwars;
   }

   @ConstructorProperties({"name", "contents", "armor", "kitEditContents", "icon", "excludedArenas", "arenaWhiteList", "enabled", "ranked", "combo", "sumo", "build", "spleef", "bedwars"})
   public Kit(String name, ItemStack[] contents, ItemStack[] armor, ItemStack[] kitEditContents, ItemStack icon, List<String> excludedArenas, List<String> arenaWhiteList, boolean enabled, boolean ranked, boolean combo, boolean sumo, boolean build, boolean spleef, boolean bedwars) {
      this.name = name;
      this.contents = contents;
      this.armor = armor;
      this.kitEditContents = kitEditContents;
      this.icon = icon;
      this.excludedArenas = excludedArenas;
      this.arenaWhiteList = arenaWhiteList;
      this.enabled = enabled;
      this.ranked = ranked;
      this.combo = combo;
      this.sumo = sumo;
      this.build = build;
      this.spleef = spleef;
      this.bedwars = bedwars;
   }

   @ConstructorProperties({"name"})
   public Kit(String name) {
      this.name = name;
   }
}
