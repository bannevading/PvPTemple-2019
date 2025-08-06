package server.pvptemple.events.corners;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.cuboid.Cuboid;
import server.pvptemple.util.finalutil.CC;

public class FourCornersEvent extends PracticeEvent<FourCornersPlayer> {
   private final Map<UUID, FourCornersPlayer> players = new HashMap();
   private final FourCornersCountdownTask countdownTask = new FourCornersCountdownTask(this);
   private RunnerGameTask gameTask;
   private MoveTask moveTask;
   private RemoveBlocksTask removeBlocksTask;
   private Map<Location, ItemStack> blocks;
   private int seconds;
   private int randomWool;
   private int round;
   private boolean running = false;
   private Cuboid zone;

   public FourCornersEvent() {
      super("4Corners");
   }

   public Map<UUID, FourCornersPlayer> getPlayers() {
      return this.players;
   }

   public EventCountdownTask getCountdownTask() {
      return this.countdownTask;
   }

   public List<CustomLocation> getSpawnLocations() {
      return Collections.singletonList(this.getPlugin().getSpawnManager().getCornersLocation());
   }

   public void onStart() {
      this.seconds = 11;
      this.round = 1;
      this.gameTask = new RunnerGameTask();
      this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
      this.blocks = new HashMap();
      this.zone = new Cuboid(this.getPlugin().getSpawnManager().getCornersMin().toBukkitLocation(), this.getPlugin().getSpawnManager().getCornersMax().toBukkitLocation());
   }

   private void cancelAll() {
      if (this.gameTask != null) {
         this.gameTask.cancel();
      }

      if (this.moveTask != null) {
         this.moveTask.cancel();
      }

      if (this.removeBlocksTask != null) {
         this.removeBlocksTask.cancel();
      }

      this.running = false;
      this.zone = null;
   }

   public Consumer<Player> onJoin() {
      return (player) -> {
         FourCornersPlayer var10000 = (FourCornersPlayer)this.players.put(player.getUniqueId(), new FourCornersPlayer(player.getUniqueId(), this));
      };
   }

   public Consumer<Player> onDeath() {
      return (player) -> {
         FourCornersPlayer data = (FourCornersPlayer)this.getPlayer(player);
         if (data.getState() == FourCornersPlayer.FourCornerState.INGAME) {
            data.setState(FourCornersPlayer.FourCornerState.ELIMINATED);
            this.getPlugin().getEventManager().addSpectatorCorners(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
            this.sendMessage("&8[&9Event&8] " + player.getDisplayName() + " &6was eliminated.");
            player.sendMessage(" ");
            player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && !mineman.hasRank(Rank.PRIME)) {
               player.sendMessage(CC.GRAY + "Purchase a rank at shop.pvptemple.com to host events of your own.");
            }

            player.sendMessage(" ");
            if (this.getByState(FourCornersPlayer.FourCornerState.INGAME).size() == 1) {
               Player winner = Bukkit.getPlayer((UUID)this.getByState(FourCornersPlayer.FourCornerState.INGAME).get(0));
               if (winner != null) {
                  Bukkit.broadcastMessage("");
                  Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &e4Corners&6 event."));
                  Bukkit.broadcastMessage("");
               }

               this.end();
               this.cancelAll();
               Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
                  this.blocks.forEach((location, stack) -> location.getBlock().setTypeIdAndData(stack.getTypeId(), (byte)stack.getDurability(), true));
                  if (this.blocks.size() > 0) {
                     this.blocks.clear();
                  }

               }, 40L);
            }

         }
      };
   }

   public List<UUID> getByState(FourCornersPlayer.FourCornerState state) {
      return (List)this.players.values().stream().filter((player) -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
   }

   private Player getRandomPlayer() {
      if (this.getByState(FourCornersPlayer.FourCornerState.INGAME).size() == 0) {
         return null;
      } else {
         List<UUID> fighting = this.getByState(FourCornersPlayer.FourCornerState.INGAME);
         Collections.shuffle(fighting);
         UUID uuid = (UUID)fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));
         return this.getPlugin().getServer().getPlayer(uuid);
      }
   }

   private void handleRemoveBridges(boolean bridges) {
      this.randomWool = this.getRandomWool();
      this.zone.forEach((block) -> {
         if (bridges) {
            if (!block.getType().equals(Material.WOOL)) {
               this.blocks.put(block.getLocation(), new ItemStack(block.getType(), 1, (short)block.getData()));
               block.setType(Material.AIR);
            }
         } else if (block.getType().equals(Material.WOOL) && block.getData() == (byte)this.randomWool) {
            this.blocks.put(block.getLocation(), new ItemStack(block.getType(), 1, (short)this.randomWool));
            block.setType(Material.AIR);
            block.getLocation().getWorld().strikeLightningEffect(block.getLocation());
         }

      });
      if (!bridges) {
         this.sendMessage("&8[&9Round " + this.round + "&8] " + (this.randomWool == 14 ? "&cRed" : (this.randomWool == 3 ? "&bBlue" : (this.randomWool == 5 ? "&aGreen" : "&eYellow"))) + " &6was dropped.");
      }

   }

   private int getRandomWool() {
      List<Integer> wools = Arrays.asList(14, 3, 5, 4);
      return (Integer)wools.get(ThreadLocalRandom.current().nextInt(wools.size()));
   }

   public RunnerGameTask getGameTask() {
      return this.gameTask;
   }

   public MoveTask getMoveTask() {
      return this.moveTask;
   }

   public RemoveBlocksTask getRemoveBlocksTask() {
      return this.removeBlocksTask;
   }

   public Map<Location, ItemStack> getBlocks() {
      return this.blocks;
   }

   public int getSeconds() {
      return this.seconds;
   }

   public int getRound() {
      return this.round;
   }

   public boolean isRunning() {
      return this.running;
   }

   public Cuboid getZone() {
      return this.zone;
   }

   public class RunnerGameTask extends BukkitRunnable {
      private int time = 303;

      public void run() {
         if (this.time == 303) {
            FourCornersEvent.this.getBukkitPlayers().stream().filter((player) -> FourCornersEvent.this.getPlayers().containsKey(player.getUniqueId())).forEach((player) -> {
               Block legs = player.getLocation().getBlock();
               Block head = legs.getRelative(BlockFace.UP);
               if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                  player.teleport(FourCornersEvent.this.getPlugin().getSpawnManager().getCornersLocation().toBukkitLocation());
               }

            });
            FourCornersEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "3 seconds" + CC.GOLD + ".");
         } else if (this.time == 302) {
            FourCornersEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "2 seconds" + CC.GOLD + ".");
         } else if (this.time == 301) {
            FourCornersEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "1 second" + CC.GOLD + ".");
         } else if (this.time == 300) {
            FourCornersEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The game has started.");
            FourCornersEvent.this.getPlayers().values().forEach((player) -> player.setState(FourCornersPlayer.FourCornerState.INGAME));
            FourCornersEvent.this.getBukkitPlayers().forEach((player) -> player.getInventory().clear());
            FourCornersEvent.this.moveTask = FourCornersEvent.this.new MoveTask();
            FourCornersEvent.this.moveTask.runTaskTimer(FourCornersEvent.this.getPlugin(), 0L, 1L);
            FourCornersEvent.this.removeBlocksTask = FourCornersEvent.this.new RemoveBlocksTask();
            FourCornersEvent.this.removeBlocksTask.runTaskTimer(FourCornersEvent.this.getPlugin(), 0L, 20L);
            FourCornersEvent.this.running = true;
         } else if (this.time <= 0) {
            Player winner = FourCornersEvent.this.getRandomPlayer();
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &e4Corners&6 event."));
               Bukkit.broadcastMessage("");
            }

            FourCornersEvent.this.end();
            FourCornersEvent.this.cancelAll();
            this.cancel();
            return;
         }

         if (FourCornersEvent.this.getPlayers().size() == 1) {
            Player winner = Bukkit.getPlayer((UUID)FourCornersEvent.this.getByState(FourCornersPlayer.FourCornerState.INGAME).get(0));
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &e4Corners&6 event."));
               Bukkit.broadcastMessage("");
            }

            FourCornersEvent.this.end();
            FourCornersEvent.this.cancelAll();
            this.cancel();
         } else {
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
               FourCornersEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game ends in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
               FourCornersEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game is ending in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            }

            --this.time;
         }
      }

      public int getTime() {
         return this.time;
      }
   }

   private class MoveTask extends BukkitRunnable {
      public void run() {
         FourCornersEvent.this.getBukkitPlayers().forEach((player) -> {
            if (FourCornersEvent.this.getPlayer(player.getUniqueId()) != null && ((FourCornersPlayer)FourCornersEvent.this.getPlayer(player.getUniqueId())).getState() == FourCornersPlayer.FourCornerState.INGAME) {
               if (FourCornersEvent.this.getPlayers().size() <= 1) {
                  return;
               }

               if (FourCornersEvent.this.getPlayers().containsKey(player.getUniqueId())) {
                  Block legs = player.getLocation().getBlock();
                  Block head = legs.getRelative(BlockFace.UP);
                  if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                     FourCornersEvent.this.onDeath().accept(player);
                  }
               }
            }

         });
      }

      public MoveTask() {
      }
   }

   private class RemoveBlocksTask extends BukkitRunnable {
      public void run() {
         if (FourCornersEvent.this.running) {
            FourCornersEvent.this.seconds--;
            if (FourCornersEvent.this.seconds <= 0) {
               FourCornersEvent.this.running = false;
               FourCornersEvent.this.handleRemoveBridges(true);
               Bukkit.getScheduler().runTaskLater(FourCornersEvent.this.getPlugin(), () -> {
                  FourCornersEvent.this.handleRemoveBridges(false);
                  Bukkit.getScheduler().runTaskLater(FourCornersEvent.this.getPlugin(), () -> {
                     FourCornersEvent.this.blocks.forEach((location, stack) -> location.getBlock().setTypeIdAndData(stack.getTypeId(), (byte)stack.getDurability(), true));
                     if (FourCornersEvent.this.blocks.size() > 0) {
                        FourCornersEvent.this.blocks.clear();
                     }

                  }, 60L);
                  Bukkit.getScheduler().runTaskLater(FourCornersEvent.this.getPlugin(), () -> {
                     FourCornersEvent.this.round++;
                     FourCornersEvent.this.seconds = 11;
                     FourCornersEvent.this.running = true;
                  }, 100L);
               }, 60L);
            } else {
               if (Arrays.asList(10, 5, 4, 3, 2, 1).contains(FourCornersEvent.this.seconds)) {
                  FourCornersEvent.this.sendMessage("&8[&9Round " + FourCornersEvent.this.round + "&8] &6Bridges dropping in &e" + FourCornersEvent.this.seconds + " " + (FourCornersEvent.this.seconds == 1 ? "second" : "seconds") + "&6.");
               }

            }
         }
      }

      public RemoveBlocksTask() {
      }
   }
}
