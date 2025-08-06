package server.pvptemple.events;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.event.EventStartEvent;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.oitc.OITCPlayer;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.events.sumo.SumoPlayer;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;

public abstract class PracticeEvent<K extends EventPlayer> {
   private final Carbon plugin = Carbon.getInstance();
   private final String name;
   private Player host;
   private int limit = 30;
   private EventState state;

   public void startCountdown() {
      if (this.getCountdownTask().isEnded()) {
         this.getCountdownTask().setTimeUntilStart(this.getCountdownTask().getCountdownTime());
         this.getCountdownTask().setEnded(false);
      } else {
         this.getCountdownTask().runTaskTimer(this.plugin, 20L, 20L);
      }

   }

   public void sendMessage(String message) {
      this.getBukkitPlayers().forEach((player) -> player.sendMessage(Color.translate(message)));
   }

   public Set<Player> getBukkitPlayers() {
      Stream<UUID> var10000 = this.getPlayers().keySet().stream().filter((uuid) -> this.plugin.getServer().getPlayer(uuid) != null);
      Server var10001 = this.plugin.getServer();
      return var10000.map(var10001::getPlayer).collect(Collectors.toSet());
   }

   public void join(Player player) {
      if (this.getPlayers().size() < this.limit) {
         this.plugin.getQueueManager().removePlayerFromQueue(player);
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         playerData.setPlayerState(PlayerState.EVENT);
         PlayerUtil.clearPlayer(player);
         if (this.onJoin() != null) {
            this.onJoin().accept(player);
         }

         if (this.getSpawnLocations().size() == 1) {
            player.teleport(((CustomLocation)this.getSpawnLocations().get(0)).toBukkitLocation());
         } else {
            List<CustomLocation> spawnLocations = new ArrayList(this.getSpawnLocations());
            player.teleport(((CustomLocation)spawnLocations.get(ThreadLocalRandom.current().nextInt(spawnLocations.size()))).toBukkitLocation());
         }

         this.plugin.getPlayerManager().giveLobbyItems(player);
         this.getBukkitPlayers().forEach((other) -> other.showPlayer(player));
         this.getBukkitPlayers().forEach(player::showPlayer);
         this.sendMessage("&8[&9Event&8] " + player.getDisplayName() + " &6has joined. &7(&f" + this.getPlayers().size() + " player" + (this.getPlayers().size() == 1 ? "" : "s") + "&7)");
         player.sendMessage(CC.GREEN + "You have joined " + CC.DARK_GREEN + this.name + CC.GREEN + ".");
      }
   }

   public void leave(Player player) {
      if (this instanceof OITCEvent) {
         OITCEvent oitcEvent = (OITCEvent)this;
         OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(player);
         oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
      }

      if (this.onDeath() != null) {
         this.onDeath().accept(player);
      }

      this.getPlayers().remove(player.getUniqueId());
      this.plugin.getPlayerManager().sendToSpawnAndReset(player);
   }

   public void start() {
      (new EventStartEvent(this)).call();
      this.setState(EventState.STARTED);
      this.onStart();
      this.plugin.getEventManager().setCooldown(0L);
   }

   public void end() {
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.plugin.getEventManager().getEventWorld().getPlayers().forEach((player) -> this.plugin.getPlayerManager().sendToSpawnAndReset(player)), 2L);
      this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + 180000L);
      if (this instanceof SumoEvent) {
         SumoEvent sumoEvent = (SumoEvent)this;

         for(SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {
            if (sumoPlayer.getFightTask() != null) {
               sumoPlayer.getFightTask().cancel();
            }
         }

         if (sumoEvent.getWaterCheckTask() != null) {
            sumoEvent.getWaterCheckTask().cancel();
         }
      } else if (this instanceof RunnerEvent) {
         RunnerEvent runnerEvent = (RunnerEvent)this;
         runnerEvent.cancelAll();
      } else if (this instanceof LMSEvent) {
         LMSEvent lmsEvent = (LMSEvent)this;
         lmsEvent.cancelAll();
         Bukkit.getWorld("event").getEntities().stream().filter((entity) -> entity instanceof Item).forEach(Entity::remove);
      } else if (this instanceof OITCEvent) {
         OITCEvent oitcEvent = (OITCEvent)this;
         if (oitcEvent.getGameTask() != null) {
            oitcEvent.getGameTask().cancel();
         }

         oitcEvent.setRunning(false);
      } else if (this instanceof ParkourEvent) {
         ParkourEvent parkourEvent = (ParkourEvent)this;
         if (parkourEvent.getGameTask() != null) {
            parkourEvent.getGameTask().cancel();
         }

         if (parkourEvent.getWaterCheckTask() != null) {
            parkourEvent.getWaterCheckTask().cancel();
         }
      }

      this.getPlayers().clear();
      this.setState(EventState.UNANNOUNCED);
      Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().keySet().iterator();

      while(iterator.hasNext()) {
         UUID spectatorUUID = (UUID)iterator.next();
         Player spectator = Bukkit.getPlayer(spectatorUUID);
         if (spectator != null) {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getPlayerManager().sendToSpawnAndReset(spectator));
            iterator.remove();
         }
      }

      this.plugin.getEventManager().getSpectators().clear();
      this.getCountdownTask().setEnded(true);
   }

   public K getPlayer(Player player) {
      return (K)this.getPlayer(player.getUniqueId());
   }

   public K getPlayer(UUID uuid) {
      return (K)(this.getPlayers().get(uuid));
   }

   public abstract Map<UUID, K> getPlayers();

   public abstract EventCountdownTask getCountdownTask();

   public abstract List<CustomLocation> getSpawnLocations();

   public abstract void onStart();

   public abstract Consumer<Player> onJoin();

   public abstract Consumer<Player> onDeath();

   public Carbon getPlugin() {
      return this.plugin;
   }

   public String getName() {
      return this.name;
   }

   public Player getHost() {
      return this.host;
   }

   public int getLimit() {
      return this.limit;
   }

   public EventState getState() {
      return this.state;
   }

   public void setHost(Player host) {
      this.host = host;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public void setState(EventState state) {
      this.state = state;
   }

   @ConstructorProperties({"name"})
   public PracticeEvent(String name) {
      this.state = EventState.UNANNOUNCED;
      this.name = name;
   }
}
