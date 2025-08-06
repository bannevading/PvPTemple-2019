package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import server.pvptemple.Carbon;
import server.pvptemple.arena.Arena;
import server.pvptemple.arena.StandaloneArena;
import server.pvptemple.kit.Kit;
import server.pvptemple.util.Config;
import server.pvptemple.util.CustomLocation;

public class ArenaManager {
   private final Carbon plugin = Carbon.getInstance();
   private final Config config;
   private final Map<String, Arena> arenas;
   private final Map<StandaloneArena, UUID> arenaMatchUUIDs;
   private int generatingArenaRunnables;

   public ArenaManager() {
      this.config = new Config("arenas", this.plugin);
      this.arenas = new HashMap();
      this.arenaMatchUUIDs = new HashMap();
      this.loadArenas();
   }

   private void loadArenas() {
      FileConfiguration fileConfig = this.config.getConfig();
      ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
      if (arenaSection != null) {
         arenaSection.getKeys(false).forEach((name) -> {
            String a = arenaSection.getString(name + ".a");
            String b = arenaSection.getString(name + ".b");
            String min = arenaSection.getString(name + ".min");
            String max = arenaSection.getString(name + ".max");
            CustomLocation locA = CustomLocation.stringToLocation(a);
            CustomLocation locB = CustomLocation.stringToLocation(b);
            CustomLocation locMin = CustomLocation.stringToLocation(min);
            CustomLocation locMax = CustomLocation.stringToLocation(max);
            List<StandaloneArena> standaloneArenas = new ArrayList();
            ConfigurationSection saSection = arenaSection.getConfigurationSection(name + ".standaloneArenas");
            if (saSection != null) {
               saSection.getKeys(false).forEach((id) -> {
                  String saA = saSection.getString(id + ".a");
                  String saB = saSection.getString(id + ".b");
                  String saMin = saSection.getString(id + ".min");
                  String saMax = saSection.getString(id + ".max");
                  CustomLocation locSaA = CustomLocation.stringToLocation(saA);
                  CustomLocation locSaB = CustomLocation.stringToLocation(saB);
                  CustomLocation locSaMin = CustomLocation.stringToLocation(saMin);
                  CustomLocation locSaMax = CustomLocation.stringToLocation(saMax);
                  StandaloneArena arena = new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax, (CustomLocation)null, (CustomLocation)null);
                  if (saSection.contains(id + ".bedA")) {
                     arena.setBedA(CustomLocation.stringToLocation(saSection.getString(id + ".bedA")));
                  }

                  if (saSection.contains(id + ".bedB")) {
                     arena.setBedB(CustomLocation.stringToLocation(saSection.getString(id + ".bedB")));
                  }

                  standaloneArenas.add(arena);
               });
            }

            boolean enabled = arenaSection.getBoolean(name + ".enabled", false);
            Arena arena = new Arena(name, standaloneArenas, new ArrayList(standaloneArenas), locA, locB, locMin, locMax, (CustomLocation)null, (CustomLocation)null, enabled);
            if (arenaSection.contains(name + ".bedA")) {
               arena.setBedA(CustomLocation.stringToLocation(arenaSection.getString(name + ".bedA")));
            }

            if (arenaSection.contains(name + ".bedB")) {
               arena.setBedB(CustomLocation.stringToLocation(arenaSection.getString(name + ".bedB")));
            }

            this.arenas.put(name, arena);
         });
      }
   }

   public void saveArenas() {
      FileConfiguration fileConfig = this.config.getConfig();
      fileConfig.set("arenas", (Object)null);
      this.arenas.forEach((arenaName, arena) -> {
         String a = CustomLocation.locationToString(arena.getA());
         String b = CustomLocation.locationToString(arena.getB());
         String min = CustomLocation.locationToString(arena.getMin());
         String max = CustomLocation.locationToString(arena.getMax());
         String arenaRoot = "arenas." + arenaName;
         fileConfig.set(arenaRoot + ".a", a);
         fileConfig.set(arenaRoot + ".b", b);
         fileConfig.set(arenaRoot + ".min", min);
         fileConfig.set(arenaRoot + ".max", max);
         if (arena.getBedA() != null) {
            fileConfig.set(arenaRoot + ".bedA", CustomLocation.locationToString(arena.getBedA()));
         }

         if (arena.getBedA() != null) {
            fileConfig.set(arenaRoot + ".bedB", CustomLocation.locationToString(arena.getBedB()));
         }

         fileConfig.set(arenaRoot + ".enabled", arena.isEnabled());
         fileConfig.set(arenaRoot + ".standaloneArenas", (Object)null);
         int i = 0;
         if (arena.getStandaloneArenas() != null) {
            for(StandaloneArena saArena : arena.getStandaloneArenas()) {
               String saA = CustomLocation.locationToString(saArena.getA());
               String saB = CustomLocation.locationToString(saArena.getB());
               String saMin = CustomLocation.locationToString(saArena.getMin());
               String saMax = CustomLocation.locationToString(saArena.getMax());
               String standAloneRoot = arenaRoot + ".standaloneArenas." + i;
               fileConfig.set(standAloneRoot + ".a", saA);
               fileConfig.set(standAloneRoot + ".b", saB);
               fileConfig.set(standAloneRoot + ".min", saMin);
               fileConfig.set(standAloneRoot + ".max", saMax);
               if (saArena.getBedA() != null) {
                  fileConfig.set(standAloneRoot + ".bedA", CustomLocation.locationToString(saArena.getBedA()));
               }

               if (saArena.getBedA() != null) {
                  fileConfig.set(standAloneRoot + ".bedB", CustomLocation.locationToString(saArena.getBedB()));
               }

               ++i;
            }
         }

      });
      this.config.save();
   }

   public void createArena(String name) {
      this.arenas.put(name, new Arena(name));
   }

   public void deleteArena(String name) {
      this.arenas.remove(name);
   }

   public Arena getArena(String name) {
      return (Arena)this.arenas.get(name);
   }

   public Arena getRandomArena(Kit kit) {
      List<Arena> enabledArenas = new ArrayList();

      for(Arena arena : this.arenas.values()) {
         if (arena.isEnabled() && !kit.getExcludedArenas().contains(arena.getName()) && (kit.getArenaWhiteList().size() <= 0 || kit.getArenaWhiteList().contains(arena.getName()))) {
            enabledArenas.add(arena);
         }
      }

      if (enabledArenas.size() == 0) {
         return null;
      } else {
         return (Arena)enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
      }
   }

   public void removeArenaMatchUUID(StandaloneArena arena) {
      this.arenaMatchUUIDs.remove(arena);
   }

   public UUID getArenaMatchUUID(StandaloneArena arena) {
      return (UUID)this.arenaMatchUUIDs.get(arena);
   }

   public void setArenaMatchUUID(StandaloneArena arena, UUID matchUUID) {
      this.arenaMatchUUIDs.put(arena, matchUUID);
   }

   public Map<String, Arena> getArenas() {
      return this.arenas;
   }

   public Map<StandaloneArena, UUID> getArenaMatchUUIDs() {
      return this.arenaMatchUUIDs;
   }

   public int getGeneratingArenaRunnables() {
      return this.generatingArenaRunnables;
   }

   public void setGeneratingArenaRunnables(int generatingArenaRunnables) {
      this.generatingArenaRunnables = generatingArenaRunnables;
   }
}
