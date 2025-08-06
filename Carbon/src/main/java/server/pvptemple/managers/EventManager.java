package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.EventState;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.corners.FourCornersEvent;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.CustomLocation;

public class EventManager {
   private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap();
   private HashMap<UUID, PracticeEvent> spectators;
   private final Carbon plugin = Carbon.getInstance();
   private final World eventWorld;
   private boolean enabled = true;
   private long cooldown;

   public EventManager() {
      Arrays.asList(FourCornersEvent.class, LMSEvent.class, OITCEvent.class, ParkourEvent.class, SumoEvent.class, RunnerEvent.class).forEach(this::addEvent);
      boolean newWorld;
      if (Bukkit.getWorld("event") == null) {
         this.eventWorld = Bukkit.createWorld(new WorldCreator("event"));
         newWorld = true;
      } else {
         this.eventWorld = Bukkit.getWorld("event");
         newWorld = false;
      }

      this.cooldown = 0L;
      this.spectators = new HashMap();
      if (this.eventWorld != null) {
         if (newWorld) {
            Bukkit.getWorlds().add(this.eventWorld);
         }

         this.eventWorld.setTime(2000L);
         this.eventWorld.setGameRuleValue("doDaylightCycle", "false");
         this.eventWorld.setGameRuleValue("doMobSpawning", "false");
         this.eventWorld.setStorm(false);
         this.eventWorld.getEntities().stream().filter((entity) -> !(entity instanceof Player)).forEach(Entity::remove);
      }

   }

   public PracticeEvent getByName(String name) {
      return this.events.values().stream().filter((event) -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
   }

   public void hostEvent(PracticeEvent event, Player host) {
      event.setState(EventState.WAITING);
      event.setHost(host);
      event.setLimit(this.getLimit(host));
      event.startCountdown();
   }

   private int getLimit(Player player) {
      switch (CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getRank()) {
         case OWNER:
         case PLATFORMADMIN:
         case DEVELOPER:
            return 150;
         case MASTER:
            return 50;
         default:
            return 30;
      }
   }

   private void addEvent(Class<? extends PracticeEvent> clazz) {
      PracticeEvent event = null;

      try {
         event = (PracticeEvent)clazz.newInstance();
      } catch (IllegalAccessException | InstantiationException e) {
         ((ReflectiveOperationException)e).printStackTrace();
      }

      this.events.put(clazz, event);
   }

   public boolean isPlaying(Player player, PracticeEvent event) {
      return event.getPlayers().containsKey(player.getUniqueId());
   }

   public void addSpectatorSumo(Player player, PlayerData playerData, SumoEvent event) {
      this.addSpectator(player, playerData, event);
      if (event.getSpawnLocations().size() == 1) {
         player.teleport(((CustomLocation)event.getSpawnLocations().get(0)).toBukkitLocation());
      } else {
         List<CustomLocation> spawnLocations = new ArrayList(event.getSpawnLocations());
         player.teleport(((CustomLocation)spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());
      }

      for(Player eventPlayer : event.getBukkitPlayers()) {
         player.showPlayer(eventPlayer);
      }

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
   }

   public void addSpectatorRunner(Player player, PlayerData playerData, RunnerEvent event) {
      this.addSpectator(player, playerData, event);
      List<CustomLocation> spawnLocations = new ArrayList(event.getSpawnLocations());
      player.teleport(((CustomLocation)spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());

      for(Player eventPlayer : event.getBukkitPlayers()) {
         player.showPlayer(eventPlayer);
      }

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
   }

   public void addSpectatorLMS(Player player, PlayerData playerData, LMSEvent event) {
      if (player.isDead()) {
         player.setHealth((double)20.0F);
      }

      this.addSpectator(player, playerData, event);
      List<CustomLocation> spawnLocations = new ArrayList(event.getSpawnLocations());
      player.teleport(((CustomLocation)spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());

      for(Player eventPlayer : event.getBukkitPlayers()) {
         player.showPlayer(eventPlayer);
      }

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
   }

   public void addSpectatorParkour(Player player, PlayerData playerData, ParkourEvent event) {
      this.addSpectator(player, playerData, event);
      if (event.getSpawnLocations().size() == 1) {
         player.teleport(((CustomLocation)event.getSpawnLocations().get(0)).toBukkitLocation());
      } else {
         List<CustomLocation> spawnLocations = new ArrayList(event.getSpawnLocations());
         player.teleport(((CustomLocation)spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());
      }

      for(Player eventPlayer : event.getBukkitPlayers()) {
         player.showPlayer(eventPlayer);
      }

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
   }

   public void addSpectatorOITC(Player player, PlayerData playerData, OITCEvent event) {
      this.addSpectator(player, playerData, event);
      if (event.getSpawnLocations().size() == 1) {
         player.teleport(((CustomLocation)event.getSpawnLocations().get(0)).toBukkitLocation());
      } else {
         List<CustomLocation> spawnLocations = new ArrayList(event.getSpawnLocations());
         player.teleport(((CustomLocation)spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());
      }

      for(Player eventPlayer : event.getBukkitPlayers()) {
         player.showPlayer(eventPlayer);
      }

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
   }

   public void addSpectatorCorners(Player player, PlayerData playerData, FourCornersEvent event) {
      this.addSpectator(player, playerData, event);
      if (event.getSpawnLocations().size() == 1) {
         player.teleport(((CustomLocation)event.getSpawnLocations().get(0)).toBukkitLocation());
      } else {
         List<CustomLocation> spawnLocations = new ArrayList(event.getSpawnLocations());
         player.teleport(((CustomLocation)spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());
      }

      for(Player eventPlayer : event.getBukkitPlayers()) {
         player.showPlayer(eventPlayer);
      }

      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
   }

   private void addSpectator(Player player, PlayerData playerData, PracticeEvent event) {
      playerData.setPlayerState(PlayerState.SPECTATING);
      this.spectators.put(player.getUniqueId(), event);
      player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
      player.updateInventory();
      Bukkit.getOnlinePlayers().forEach((online) -> {
         online.hidePlayer(player);
         player.hidePlayer(online);
      });
   }

   public void removeSpectator(Player player) {
      this.spectators.remove(player.getUniqueId());
      if (this.plugin.getEventManager().getEventPlaying(player) != null) {
         this.plugin.getEventManager().getEventPlaying(player).getPlayers().remove(player.getUniqueId());
      }

      this.plugin.getPlayerManager().sendToSpawnAndReset(player);
   }

   public PracticeEvent getEventPlaying(Player player) {
      return (PracticeEvent)this.events.values().stream().filter((event) -> this.isPlaying(player, event)).findFirst().orElse(null);
   }

   public Map<Class<? extends PracticeEvent>, PracticeEvent> getEvents() {
      return this.events;
   }

   public HashMap<UUID, PracticeEvent> getSpectators() {
      return this.spectators;
   }

   public Carbon getPlugin() {
      return this.plugin;
   }

   public World getEventWorld() {
      return this.eventWorld;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public long getCooldown() {
      return this.cooldown;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void setCooldown(long cooldown) {
      this.cooldown = cooldown;
   }
}
