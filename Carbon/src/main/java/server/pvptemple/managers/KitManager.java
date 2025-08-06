package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.Carbon;
import server.pvptemple.kit.Kit;
import server.pvptemple.util.Config;

public class KitManager {
   private final Carbon plugin = Carbon.getInstance();
   private final Map<String, Kit> kits = new HashMap();
   private final List<String> rankedKits = new ArrayList();
   private final Config config;

   public KitManager() {
      this.config = new Config("kits", this.plugin);
      this.loadKits();
      this.kits.entrySet().stream().filter((kit) -> ((Kit)kit.getValue()).isEnabled()).filter((kit) -> ((Kit)kit.getValue()).isRanked()).forEach((kit) -> this.rankedKits.add(kit.getKey()));
   }

   private void loadKits() {
      FileConfiguration fileConfig = this.config.getConfig();
      ConfigurationSection kitSection = fileConfig.getConfigurationSection("kits");
      if (kitSection != null) {
         kitSection.getKeys(false).forEach((name) -> {
            ItemStack[] contents = (ItemStack[])((List)kitSection.get(name + ".contents")).toArray(new ItemStack[0]);
            ItemStack[] armor = (ItemStack[])((List)kitSection.get(name + ".armor")).toArray(new ItemStack[0]);
            ItemStack[] kitEditContents = (ItemStack[])((List)kitSection.get(name + ".kitEditContents")).toArray(new ItemStack[0]);
            List<String> excludedArenas = kitSection.getStringList(name + ".excludedArenas");
            List<String> arenaWhiteList = kitSection.getStringList(name + ".arenaWhitelist");
            ItemStack icon = (ItemStack)kitSection.get(name + ".icon");
            boolean enabled = kitSection.getBoolean(name + ".enabled");
            boolean ranked = kitSection.getBoolean(name + ".ranked");
            boolean combo = kitSection.getBoolean(name + ".combo");
            boolean sumo = kitSection.getBoolean(name + ".sumo");
            boolean build = kitSection.getBoolean(name + ".build");
            boolean spleef = kitSection.getBoolean(name + ".spleef");
            boolean bedwars = kitSection.getBoolean(name + ".bedwars");
            Kit kit = new Kit(name, contents, armor, kitEditContents, icon, excludedArenas, arenaWhiteList, enabled, ranked, combo, sumo, build, spleef, bedwars);
            this.kits.put(name, kit);
         });
      }
   }

   public void saveKits() {
      FileConfiguration fileConfig = this.config.getConfig();
      fileConfig.set("kits", (Object)null);
      this.kits.forEach((kitName, kit) -> {
         if (kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
            fileConfig.set("kits." + kitName + ".contents", kit.getContents());
            fileConfig.set("kits." + kitName + ".armor", kit.getArmor());
            fileConfig.set("kits." + kitName + ".kitEditContents", kit.getKitEditContents());
            fileConfig.set("kits." + kitName + ".icon", kit.getIcon());
            fileConfig.set("kits." + kitName + ".excludedArenas", kit.getExcludedArenas());
            fileConfig.set("kits." + kitName + ".arenaWhitelist", kit.getArenaWhiteList());
            fileConfig.set("kits." + kitName + ".enabled", kit.isEnabled());
            fileConfig.set("kits." + kitName + ".ranked", kit.isRanked());
            fileConfig.set("kits." + kitName + ".combo", kit.isCombo());
            fileConfig.set("kits." + kitName + ".sumo", kit.isSumo());
            fileConfig.set("kits." + kitName + ".build", kit.isBuild());
            fileConfig.set("kits." + kitName + ".spleef", kit.isSpleef());
            fileConfig.set("kits." + kitName + ".bedwars", kit.isBedwars());
         }

      });
      this.config.save();
   }

   public void deleteKit(String name) {
      this.kits.remove(name);
   }

   public void createKit(String name) {
      this.kits.put(name, new Kit(name));
   }

   public Collection<Kit> getKits() {
      return this.kits.values();
   }

   public Kit getKit(String name) {
      return (Kit)this.kits.get(name);
   }

   public List<String> getRankedKits() {
      return this.rankedKits;
   }
}
