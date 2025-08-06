package server.pvptemple.events.sumo;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;

public class SumoEvent extends PracticeEvent<SumoPlayer> {
   private final Map<UUID, SumoPlayer> players = new HashMap();
   final Set<String> fighting = new HashSet();
   private final SumoCountdownTask countdownTask = new SumoCountdownTask(this);
   private WaterCheckTask waterCheckTask;

   public SumoEvent() {
      super("Sumo");
   }

   public Map<UUID, SumoPlayer> getPlayers() {
      return this.players;
   }

   public EventCountdownTask getCountdownTask() {
      return this.countdownTask;
   }

   public List<CustomLocation> getSpawnLocations() {
      return Collections.singletonList(this.getPlugin().getSpawnManager().getSumoLocation());
   }

   public void onStart() {
      this.waterCheckTask = new WaterCheckTask();
      this.waterCheckTask.runTaskTimer(this.getPlugin(), 0L, 10L);
      this.selectPlayers();
   }

   public Consumer<Player> onJoin() {
      return (player) -> {
         SumoPlayer var10000 = (SumoPlayer)this.players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
      };
   }

   public Consumer<Player> onDeath() {
      return (player) -> {
         SumoPlayer data = (SumoPlayer)this.getPlayer(player);
         if (data != null && data.getFighting() != null) {
            if (data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {
               SumoPlayer killerData = data.getFighting();
               Player killer = this.getPlugin().getServer().getPlayer(killerData.getUuid());
               data.getFightTask().cancel();
               killerData.getFightTask().cancel();
               data.setState(SumoPlayer.SumoState.ELIMINATED);
               killerData.setState(SumoPlayer.SumoState.WAITING);
               PlayerUtil.clearPlayer(player);
               this.getPlugin().getPlayerManager().giveLobbyItems(player);
               PlayerUtil.clearPlayer(killer);
               this.getPlugin().getPlayerManager().giveLobbyItems(killer);
               if (this.getSpawnLocations().size() == 1) {
                  player.teleport(((CustomLocation)this.getSpawnLocations().get(0)).toBukkitLocation());
                  killer.teleport(((CustomLocation)this.getSpawnLocations().get(0)).toBukkitLocation());
               }

               this.sendMessage("&8[&9Event&8] " + player.getDisplayName() + CC.GOLD + " was eliminated" + (killer == null ? "." : " by " + CC.YELLOW + killer.getDisplayName()) + CC.GOLD + ".");
               player.sendMessage(" ");
               player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
               if (mineman != null && !mineman.hasRank(Rank.ELITE)) {
                  player.sendMessage(CC.GRAY + "Purchase a rank at shop.pvptemple.com to host events of your own.");
               }

               player.sendMessage(" ");
               if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
                  Player winner = Bukkit.getPlayer((UUID)this.getByState(SumoPlayer.SumoState.WAITING).get(0));
                  Bukkit.broadcastMessage("");
                  Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eSumo&6 event."));
                  Bukkit.broadcastMessage("");
                  this.fighting.clear();
                  this.end();
               } else {
                  this.getPlugin().getServer().getScheduler().runTaskLater(this.getPlugin(), this::selectPlayers, 60L);
               }
            }

         }
      };
   }

   private CustomLocation[] getSumoLocations() {
      CustomLocation[] array = new CustomLocation[2];
      array[0] = this.getPlugin().getSpawnManager().getSumoFirst();
      array[1] = this.getPlugin().getSpawnManager().getSumoSecond();
      return array;
   }

   private void selectPlayers() {
      if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
         Player winner = Bukkit.getPlayer((UUID)this.getByState(SumoPlayer.SumoState.WAITING).get(0));
         Bukkit.broadcastMessage("");
         Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eSumo&6 event."));
         Bukkit.broadcastMessage("");
         this.fighting.clear();
         this.end();
      } else {
         Player picked1 = this.getRandomPlayer();
         Player picked2 = this.getRandomPlayer();
         if (picked1 != null && picked2 != null) {
            this.fighting.clear();
            SumoPlayer picked1Data = (SumoPlayer)this.getPlayer(picked1);
            SumoPlayer picked2Data = (SumoPlayer)this.getPlayer(picked2);
            picked1Data.setFighting(picked2Data);
            picked2Data.setFighting(picked1Data);
            this.fighting.add(picked1.getName());
            this.fighting.add(picked2.getName());
            PlayerUtil.clearPlayer(picked1);
            PlayerUtil.clearPlayer(picked2);
            picked1.teleport(this.getSumoLocations()[0].toBukkitLocation());
            picked2.teleport(this.getSumoLocations()[1].toBukkitLocation());

            for(Player other : this.getBukkitPlayers()) {
               if (other != null) {
                  other.showPlayer(picked1);
                  other.showPlayer(picked2);
               }
            }

            for(UUID spectatorUUID : this.getPlugin().getEventManager().getSpectators().keySet()) {
               Player spectator = Bukkit.getPlayer(spectatorUUID);
               if (spectator != null) {
                  spectator.showPlayer(picked1);
                  spectator.showPlayer(picked2);
               }
            }

            picked1.showPlayer(picked2);
            picked2.showPlayer(picked1);
            this.sendMessage("&8[&9Event&8] " + picked1.getDisplayName() + CC.GOLD + " vs. " + picked2.getDisplayName());
            BukkitTask task = (new SumoFightTask(picked1, picked2, picked1Data, picked2Data)).runTaskTimer(this.getPlugin(), 0L, 20L);
            picked1Data.setFightTask(task);
            picked2Data.setFightTask(task);
         } else {
            this.selectPlayers();
         }
      }
   }

   private Player getRandomPlayer() {
      if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 0) {
         return null;
      } else {
         List<UUID> waiting = this.getByState(SumoPlayer.SumoState.WAITING);
         Collections.shuffle(waiting);
         UUID uuid = (UUID)waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));
         ((SumoPlayer)this.getPlayer(uuid)).setState(SumoPlayer.SumoState.PREPARING);
         return this.getPlugin().getServer().getPlayer(uuid);
      }
   }

   public List<UUID> getByState(SumoPlayer.SumoState state) {
      return (List)this.players.values().stream().filter((player) -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
   }

   public Set<String> getFighting() {
      return this.fighting;
   }

   public WaterCheckTask getWaterCheckTask() {
      return this.waterCheckTask;
   }

   public class SumoFightTask extends BukkitRunnable {
      private final Player player;
      private final Player other;
      private final SumoPlayer playerSumo;
      private final SumoPlayer otherSumo;
      private int time = 90;

      public void run() {
         if (this.player != null && this.other != null && this.player.isOnline() && this.other.isOnline()) {
            if (this.time == 90) {
               SumoEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "3 seconds" + CC.GOLD + ".");
            } else if (this.time == 89) {
               SumoEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "2 seconds" + CC.GOLD + ".");
            } else if (this.time == 88) {
               SumoEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "1 second" + CC.GOLD + ".");
            } else if (this.time == 87) {
               SumoEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The match has started.");
               this.otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
               this.playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
            } else if (this.time <= 0) {
               List<Player> players = Arrays.asList(this.player, this.other);
               Player winner = (Player)players.get(ThreadLocalRandom.current().nextInt(players.size()));
               players.stream().filter((pl) -> !pl.equals(winner)).forEach((pl) -> SumoEvent.this.onDeath().accept(pl));
               this.cancel();
               return;
            }

            if (Arrays.asList(30, 25, 20, 15, 10).contains(this.time)) {
               SumoEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The match ends in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
               SumoEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The match is ending in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            }

            --this.time;
         } else {
            this.cancel();
         }
      }

      public Player getPlayer() {
         return this.player;
      }

      public Player getOther() {
         return this.other;
      }

      public SumoPlayer getPlayerSumo() {
         return this.playerSumo;
      }

      public SumoPlayer getOtherSumo() {
         return this.otherSumo;
      }

      public int getTime() {
         return this.time;
      }

      @ConstructorProperties({"player", "other", "playerSumo", "otherSumo"})
      public SumoFightTask(Player player, Player other, SumoPlayer playerSumo, SumoPlayer otherSumo) {
         this.player = player;
         this.other = other;
         this.playerSumo = playerSumo;
         this.otherSumo = otherSumo;
      }
   }

   public class WaterCheckTask extends BukkitRunnable {
      public void run() {
         if (SumoEvent.this.getPlayers().size() > 1) {
            SumoEvent.this.getBukkitPlayers().forEach((player) -> {
               if (SumoEvent.this.getPlayer(player) == null || ((SumoPlayer)SumoEvent.this.getPlayer(player)).getState() == SumoPlayer.SumoState.FIGHTING) {
                  Block legs = player.getLocation().getBlock();
                  Block head = legs.getRelative(BlockFace.UP);
                  if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                     SumoEvent.this.onDeath().accept(player);
                  }

               }
            });
         }
      }
   }
}
