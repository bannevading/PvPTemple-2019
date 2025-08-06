package server.pvptemple.events.parkour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.ItemBuilder;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;

public class ParkourEvent extends PracticeEvent<ParkourPlayer> {
   private final Map<UUID, ParkourPlayer> players = new HashMap();
   private ParkourGameTask gameTask = null;
   private final ParkourCountdownTask countdownTask = new ParkourCountdownTask(this);
   private WaterCheckTask waterCheckTask;
   private List<UUID> visibility;

   public ParkourEvent() {
      super("Parkour");
   }

   public Map<UUID, ParkourPlayer> getPlayers() {
      return this.players;
   }

   public EventCountdownTask getCountdownTask() {
      return this.countdownTask;
   }

   public List<CustomLocation> getSpawnLocations() {
      return Collections.singletonList(this.getPlugin().getSpawnManager().getParkourLocation());
   }

   public void onStart() {
      this.gameTask = new ParkourGameTask();
      this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
      this.waterCheckTask = new WaterCheckTask();
      this.waterCheckTask.runTaskTimer(this.getPlugin(), 0L, 10L);
      this.visibility = new ArrayList();
   }

   public Consumer<Player> onJoin() {
      return (player) -> {
         ParkourPlayer var10000 = (ParkourPlayer)this.players.put(player.getUniqueId(), new ParkourPlayer(player.getUniqueId(), this));
      };
   }

   public Consumer<Player> onDeath() {
      return (player) -> this.sendMessage("&8[&9Event&8] " + player.getDisplayName() + " &6has left the game.");
   }

   public void toggleVisibility(Player player) {
      if (this.visibility.contains(player.getUniqueId())) {
         this.getBukkitPlayers().forEach(player::showPlayer);
         this.visibility.remove(player.getUniqueId());
         player.sendMessage(CC.GREEN + "You are now showing players.");
      } else {
         this.getBukkitPlayers().forEach(player::hidePlayer);
         this.visibility.add(player.getUniqueId());
         player.sendMessage(CC.GREEN + "You are now hiding players.");
      }
   }

   private void teleportToSpawnOrCheckpoint(Player player) {
      ParkourPlayer parkourPlayer = (ParkourPlayer)this.getPlayer(player.getUniqueId());
      if (parkourPlayer != null && parkourPlayer.getLastCheckpoint() != null) {
         player.teleport(parkourPlayer.getLastCheckpoint().toBukkitLocation());
         player.sendMessage(Color.translate("&8[&9Event&8] &6Teleporting back to last checkpoint."));
      } else {
         player.sendMessage(Color.translate("&8[&9Event&8] &6Teleporting back to the beginning."));
         player.teleport(this.getPlugin().getSpawnManager().getParkourGameLocation().toBukkitLocation());
      }
   }

   private void giveItems(Player player) {
      this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
         PlayerUtil.clearPlayer(player);
         player.getInventory().setItem(0, (new ItemBuilder(Material.EYE_OF_ENDER)).name("&aToggle Visibility").build());
         player.getInventory().setItem(4, (new ItemBuilder(Material.NETHER_STAR)).name("&cLeave Event").build());
         player.updateInventory();
      });
   }

   private Player getRandomPlayer() {
      if (this.getByState(ParkourPlayer.ParkourState.INGAME).size() == 0) {
         return null;
      } else {
         List<UUID> fighting = this.getByState(ParkourPlayer.ParkourState.INGAME);
         Collections.shuffle(fighting);
         UUID uuid = (UUID)fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));
         return this.getPlugin().getServer().getPlayer(uuid);
      }
   }

   public List<UUID> getByState(ParkourPlayer.ParkourState state) {
      return (List)this.players.values().stream().filter((player) -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
   }

   private boolean isStandingOn(Player player, Material material) {
      Block legs = player.getLocation().getBlock();
      Block head = legs.getRelative(BlockFace.UP);
      return legs.getType() == material || head.getType() == material;
   }

   private boolean isSameLocation(Location location, Location check) {
      return location.getWorld().getName().equalsIgnoreCase(check.getWorld().getName()) && location.getBlockX() == check.getBlockX() && location.getBlockY() == check.getBlockY() && location.getBlockZ() == check.getBlockZ();
   }

   public ParkourGameTask getGameTask() {
      return this.gameTask;
   }

   public WaterCheckTask getWaterCheckTask() {
      return this.waterCheckTask;
   }

   public class ParkourGameTask extends BukkitRunnable {
      private int time = 303;

      public void run() {
         if (this.time == 303) {
            ParkourEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "3 seconds" + CC.GOLD + ".");
         } else if (this.time == 302) {
            ParkourEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "2 seconds" + CC.GOLD + ".");
         } else if (this.time == 301) {
            ParkourEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "1 second" + CC.GOLD + ".");
         } else if (this.time == 300) {
            ParkourEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The game has started.");

            for(ParkourPlayer player : ParkourEvent.this.getPlayers().values()) {
               player.setLastCheckpoint((CustomLocation)null);
               player.setState(ParkourPlayer.ParkourState.INGAME);
               player.setCheckpointId(0);
            }

            for(Player player : ParkourEvent.this.getBukkitPlayers()) {
               ParkourEvent.this.teleportToSpawnOrCheckpoint(player);
               ParkourEvent.this.giveItems(player);
            }
         } else if (this.time <= 0) {
            Player winner = ParkourEvent.this.getRandomPlayer();
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eParkour&6 event."));
               Bukkit.broadcastMessage("");
            }

            ParkourEvent.this.end();
            this.cancel();
            return;
         }

         if (ParkourEvent.this.getPlayers().size() == 1) {
            Player winner = Bukkit.getPlayer((UUID)ParkourEvent.this.getByState(ParkourPlayer.ParkourState.INGAME).get(0));
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eParkour&6 event."));
               Bukkit.broadcastMessage("");
            }

            ParkourEvent.this.end();
            this.cancel();
         } else {
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
               ParkourEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game ends in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
               ParkourEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game is ending in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            }

            --this.time;
         }
      }

      public int getTime() {
         return this.time;
      }
   }

   public class WaterCheckTask extends BukkitRunnable {
      public void run() {
         if (ParkourEvent.this.getPlayers().size() > 1) {
            ParkourEvent.this.getBukkitPlayers().forEach((player) -> {
               if (ParkourEvent.this.getPlayer(player) == null || ((ParkourPlayer)ParkourEvent.this.getPlayer(player)).getState() == ParkourPlayer.ParkourState.INGAME) {
                  if (!ParkourEvent.this.isStandingOn(player, Material.WATER) && !ParkourEvent.this.isStandingOn(player, Material.STATIONARY_WATER)) {
                     if (!ParkourEvent.this.isStandingOn(player, Material.STONE_PLATE) && !ParkourEvent.this.isStandingOn(player, Material.IRON_PLATE) && !ParkourEvent.this.isStandingOn(player, Material.WOOD_PLATE)) {
                        if (ParkourEvent.this.isStandingOn(player, Material.GOLD_PLATE)) {
                           Bukkit.broadcastMessage("");
                           Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + player.getDisplayName() + CC.GOLD + " has won the &eParkour&6 event."));
                           Bukkit.broadcastMessage("");
                           ParkourEvent.this.end();
                           this.cancel();
                        }
                     } else {
                        ParkourPlayer parkourPlayer = (ParkourPlayer)ParkourEvent.this.getPlayer(player.getUniqueId());
                        if (parkourPlayer != null) {
                           boolean checkpoint = false;
                           if (parkourPlayer.getLastCheckpoint() == null) {
                              checkpoint = true;
                              parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
                           } else if (parkourPlayer.getLastCheckpoint() != null && !ParkourEvent.this.isSameLocation(player.getLocation(), parkourPlayer.getLastCheckpoint().toBukkitLocation())) {
                              checkpoint = true;
                              parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
                           }

                           if (checkpoint) {
                              parkourPlayer.setCheckpointId(parkourPlayer.getCheckpointId() + 1);
                              player.sendMessage(Color.translate("&8[&9Event&8] &6Checkpoint &e#" + parkourPlayer.getCheckpointId() + " &6has been set."));
                           }
                        }
                     }
                  } else {
                     ParkourEvent.this.teleportToSpawnOrCheckpoint(player);
                  }

               }
            });
         }
      }
   }
}
