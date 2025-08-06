package server.pvptemple.events.oitc;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;

public class OITCEvent extends PracticeEvent<OITCPlayer> {
   private final Map<UUID, OITCPlayer> players = new HashMap();
   private OITCGameTask gameTask = null;
   private final OITCCountdownTask countdownTask = new OITCCountdownTask(this);
   private List<CustomLocation> respawnLocations;
   private boolean running = false;

   public OITCEvent() {
      super("OITC");
   }

   public Map<UUID, OITCPlayer> getPlayers() {
      return this.players;
   }

   public EventCountdownTask getCountdownTask() {
      return this.countdownTask;
   }

   public List<CustomLocation> getSpawnLocations() {
      return Collections.singletonList(this.getPlugin().getSpawnManager().getOitcLocation());
   }

   public void onStart() {
      this.respawnLocations = new ArrayList();
      this.gameTask = new OITCGameTask();
      this.running = true;
      this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
   }

   public Consumer<Player> onJoin() {
      return (player) -> {
         OITCPlayer var10000 = (OITCPlayer)this.players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
      };
   }

   public Consumer<Player> onDeath() {
      return (player) -> {
         OITCPlayer data = (OITCPlayer)this.getPlayer(player);
         if (data != null && data.getState() != OITCPlayer.OITCState.WAITING) {
            if (data.getState() == OITCPlayer.OITCState.FIGHTING || data.getState() == OITCPlayer.OITCState.PREPARING || data.getState() == OITCPlayer.OITCState.RESPAWNING) {
               String deathMessage = "&8[&9Event&8] " + player.getDisplayName() + "&7[&f" + data.getScore() + "&7] &6was eliminated from the game.";
               if (data.getLastKiller() != null) {
                  OITCPlayer killerData = data.getLastKiller();
                  Player killer = Bukkit.getPlayer(killerData.getUuid());
                  int count = killerData.getScore() + 1;
                  killerData.setScore(count);
                  if (!killer.getInventory().contains(Material.ARROW)) {
                     killer.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                  } else {
                     killer.getInventory().getItem(8).setAmount(killer.getInventory().getItem(8).getAmount() + 1);
                  }

                  killer.updateInventory();
                  killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                  data.setLastKiller((OITCPlayer)null);
                  deathMessage = "&8[&9Event&8] " + player.getDisplayName() + "&7[&f" + data.getScore() + "&7] &6was killed by " + killer.getDisplayName() + "&7[&f" + count + "&7]";
                  if (count == 20) {
                     Bukkit.broadcastMessage("");
                     Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + killer.getDisplayName() + CC.GOLD + " has won the &eOITC&6 event."));
                     Bukkit.broadcastMessage("");
                     this.gameTask.cancel();
                     this.end();
                  }
               }

               if (data.getLastKiller() == null) {
                  BukkitTask respawnTask = (new RespawnTask(player, data)).runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
                  data.setRespawnTask(respawnTask);
               }

               this.sendMessage(deathMessage);
            }

         }
      };
   }

   public void teleportNextLocation(Player player) {
      player.teleport(((CustomLocation)this.getGameLocations().remove(ThreadLocalRandom.current().nextInt(this.getGameLocations().size()))).toBukkitLocation());
   }

   private List<CustomLocation> getGameLocations() {
      if (this.respawnLocations != null && this.respawnLocations.size() == 0) {
         this.respawnLocations.addAll(this.getPlugin().getSpawnManager().getOitcLocations());
      }

      return this.respawnLocations;
   }

   private void giveRespawnItems(Player player) {
      Bukkit.getScheduler().runTask(this.getPlugin(), () -> {
         PlayerUtil.clearPlayer(player);
         player.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
         player.getInventory().setItem(1, new ItemStack(Material.BOW));
         player.getInventory().setItem(8, new ItemStack(Material.ARROW));
         player.updateInventory();
      });
   }

   private Player getWinnerPlayer() {
      return this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 0 ? null : Bukkit.getPlayer(((OITCPlayer)this.sortedScores().get(0)).getUuid());
   }

   private List<UUID> getByState(OITCPlayer.OITCState state) {
      return (List)this.players.values().stream().filter((player) -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
   }

   public List<OITCPlayer> sortedScores() {
      List<OITCPlayer> list = new ArrayList(this.players.values());
      list.sort((new SortComparator()).reversed());
      return list;
   }

   public OITCGameTask getGameTask() {
      return this.gameTask;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void setRunning(boolean running) {
      this.running = running;
   }

   public class RespawnTask extends BukkitRunnable {
      private final Player player;
      private final OITCPlayer oitcPlayer;
      private int time = 3;

      public void run() {
         if (!OITCEvent.this.running) {
            this.cancel();
         } else {
            if (this.time > 0) {
               this.player.sendMessage(server.pvptemple.util.finalutil.Color.translate("&8[&9Event&8] &6Respawning in &e" + this.time + " " + (this.time == 1 ? "second" : "seconds") + "&6."));
            }

            if (this.time == 3) {
               Bukkit.getScheduler().runTask(OITCEvent.this.getPlugin(), () -> {
                  PlayerUtil.clearPlayer(this.player);
                  OITCEvent.this.getBukkitPlayers().forEach((member) -> member.hidePlayer(this.player));
                  Set<Player> var10000 = OITCEvent.this.getBukkitPlayers();
                  Player var10001 = this.player;
                  var10000.forEach(var10001::hidePlayer);
                  this.player.setGameMode(GameMode.ADVENTURE);
               });
               this.oitcPlayer.setState(OITCPlayer.OITCState.RESPAWNING);
            } else if (this.time <= 0) {
               this.player.sendMessage(server.pvptemple.util.finalutil.Color.translate("&8[&9Event&8] &6Respawning..."));
               Bukkit.getScheduler().runTaskLater(OITCEvent.this.getPlugin(), () -> {
                  OITCEvent.this.giveRespawnItems(this.player);
                  this.player.teleport(((CustomLocation)OITCEvent.this.getGameLocations().remove(ThreadLocalRandom.current().nextInt(OITCEvent.this.getGameLocations().size()))).toBukkitLocation());
                  OITCEvent.this.getBukkitPlayers().forEach((member) -> member.showPlayer(this.player));
                  Set<Player> var10000 = OITCEvent.this.getBukkitPlayers();
                  Player var10001 = this.player;
                  var10000.forEach(var10001::showPlayer);
               }, 2L);
               this.oitcPlayer.setState(OITCPlayer.OITCState.FIGHTING);
               this.cancel();
            }

            --this.time;
         }
      }

      public Player getPlayer() {
         return this.player;
      }

      public OITCPlayer getOitcPlayer() {
         return this.oitcPlayer;
      }

      public int getTime() {
         return this.time;
      }

      @ConstructorProperties({"player", "oitcPlayer"})
      public RespawnTask(Player player, OITCPlayer oitcPlayer) {
         this.player = player;
         this.oitcPlayer = oitcPlayer;
      }
   }

   public class OITCGameTask extends BukkitRunnable {
      private int time = 303;

      public void run() {
         if (this.time == 303) {
            OITCEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "3 seconds" + CC.GOLD + ".");
         } else if (this.time == 302) {
            OITCEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "2 seconds" + CC.GOLD + ".");
         } else if (this.time == 301) {
            OITCEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "1 second" + CC.GOLD + ".");
         } else if (this.time == 300) {
            OITCEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The game has started.");
            OITCEvent.this.getPlayers().values().forEach((player) -> {
               player.setScore(0);
               player.setState(OITCPlayer.OITCState.FIGHTING);
            });
            OITCEvent.this.getBukkitPlayers().forEach((player) -> {
               OITCPlayer oitcPlayer = (OITCPlayer)OITCEvent.this.getPlayer(player.getUniqueId());
               if (oitcPlayer != null) {
                  OITCEvent.this.teleportNextLocation(player);
                  OITCEvent.this.giveRespawnItems(player);
               }

            });
         } else if (this.time <= 0) {
            Player winner = OITCEvent.this.getWinnerPlayer();
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eOITC&6 event."));
               Bukkit.broadcastMessage("");
            }

            OITCEvent.this.gameTask.cancel();
            OITCEvent.this.end();
            this.cancel();
            return;
         }

         if (OITCEvent.this.getPlayers().size() == 1) {
            Player winner = Bukkit.getPlayer((UUID)OITCEvent.this.getByState(OITCPlayer.OITCState.FIGHTING).get(0));
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eOITC&6 event."));
               Bukkit.broadcastMessage("");
            }

            this.cancel();
            OITCEvent.this.end();
         }

         if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
            OITCEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game ends in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
         } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
            OITCEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game is ending in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
         }

         --this.time;
      }

      public int getTime() {
         return this.time;
      }
   }

   private class SortComparator implements Comparator<OITCPlayer> {
      private SortComparator() {
      }

      public int compare(OITCPlayer p1, OITCPlayer p2) {
         return Integer.compare(p1.getScore(), p2.getScore());
      }
   }
}
