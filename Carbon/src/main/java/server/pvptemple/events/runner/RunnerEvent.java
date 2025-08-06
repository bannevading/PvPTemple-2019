package server.pvptemple.events.runner;

import java.beans.ConstructorProperties;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.finalutil.CC;

public class RunnerEvent extends PracticeEvent<RunnerPlayer> {
   private final Map<UUID, RunnerPlayer> players = new HashMap();
   private final RunnerCountdownTask countdownTask = new RunnerCountdownTask(this);
   private List<UUID> visibility;
   private RunnerGameTask gameTask;
   private MoveTask moveTask;
   private Map<Location, ItemStack> blocks;

   public RunnerEvent() {
      super("Runner");
   }

   public Map<UUID, RunnerPlayer> getPlayers() {
      return this.players;
   }

   public EventCountdownTask getCountdownTask() {
      return this.countdownTask;
   }

   public List<CustomLocation> getSpawnLocations() {
      return this.getPlugin().getSpawnManager().getRunnerLocations();
   }

   public void onStart() {
      this.gameTask = new RunnerGameTask();
      this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
      this.visibility = new ArrayList();
      this.blocks = new HashMap();
   }

   public void cancelAll() {
      if (this.gameTask != null) {
         this.gameTask.cancel();
      }

      if (this.moveTask != null) {
         this.moveTask.cancel();
      }

      Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
         this.blocks.forEach((location, stack) -> location.getBlock().setTypeIdAndData(stack.getTypeId(), (byte)stack.getDurability(), true));
         if (this.blocks.size() > 0) {
            this.blocks.clear();
         }

      }, 40L);
   }

   public Consumer<Player> onJoin() {
      return (player) -> {
         RunnerPlayer var10000 = (RunnerPlayer)this.players.put(player.getUniqueId(), new RunnerPlayer(player.getUniqueId(), this));
      };
   }

   public Consumer<Player> onDeath() {
      return (player) -> {
         RunnerPlayer data = (RunnerPlayer)this.getPlayer(player);
         if (data.getState() == RunnerPlayer.RunnerState.INGAME) {
            data.setState(RunnerPlayer.RunnerState.ELIMINATED);
            this.getPlugin().getEventManager().addSpectatorRunner(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
            this.sendMessage("&8[&9Event&8] " + player.getDisplayName() + " &6was eliminated.");
            player.sendMessage(" ");
            player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && !mineman.hasRank(Rank.PRIME)) {
               player.sendMessage(CC.GRAY + "Purchase a rank at shop.pvptemple.com to host events of your own.");
            }

            player.sendMessage(" ");
            if (this.getByState(RunnerPlayer.RunnerState.INGAME).size() == 1) {
               Player winner = Bukkit.getPlayer((UUID)this.getByState(RunnerPlayer.RunnerState.INGAME).get(0));
               if (winner != null) {
                  Bukkit.broadcastMessage("");
                  Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eRunner&6 event."));
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

   public List<UUID> getByState(RunnerPlayer.RunnerState state) {
      return (List)this.players.values().stream().filter((player) -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
   }

   private Player getRandomPlayer() {
      if (this.getByState(RunnerPlayer.RunnerState.INGAME).size() == 0) {
         return null;
      } else {
         List<UUID> fighting = this.getByState(RunnerPlayer.RunnerState.INGAME);
         Collections.shuffle(fighting);
         UUID uuid = (UUID)fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));
         return this.getPlugin().getServer().getPlayer(uuid);
      }
   }

   private Block getBlockUnderPlayer(Location location, int y) {
      PlayerPosition loc = new PlayerPosition(location.getX(), y, location.getZ());
      Block b1 = loc.getBlock(location.getWorld(), 0.3, -0.3);
      if (b1.getType() != Material.AIR) {
         return b1;
      } else {
         Block b2 = loc.getBlock(location.getWorld(), -0.3, 0.3);
         if (b2.getType() != Material.AIR) {
            return b2;
         } else {
            Block b3 = loc.getBlock(location.getWorld(), 0.3, 0.3);
            if (b3.getType() != Material.AIR) {
               return b3;
            } else {
               Block b4 = loc.getBlock(location.getWorld(), -0.3, -0.3);
               return b4.getType() != Material.AIR ? b4 : null;
            }
         }
      }
   }

   public List<UUID> getVisibility() {
      return this.visibility;
   }

   public RunnerGameTask getGameTask() {
      return this.gameTask;
   }

   public MoveTask getMoveTask() {
      return this.moveTask;
   }

   public Map<Location, ItemStack> getBlocks() {
      return this.blocks;
   }

   public class RunnerGameTask extends BukkitRunnable {
      private int time = 303;

      public void run() {
         if (this.time == 303) {
            RunnerEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "3 seconds" + CC.GOLD + ".");
         } else if (this.time == 302) {
            RunnerEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "2 seconds" + CC.GOLD + ".");
         } else if (this.time == 301) {
            RunnerEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "1 second" + CC.GOLD + ".");
         } else if (this.time == 300) {
            RunnerEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The game has started.");
            RunnerEvent.this.getPlayers().values().forEach((player) -> player.setState(RunnerPlayer.RunnerState.INGAME));
            RunnerEvent.this.getBukkitPlayers().forEach((player) -> player.getInventory().clear());
            RunnerEvent.this.moveTask = RunnerEvent.this.new MoveTask();
            RunnerEvent.this.moveTask.runTaskTimer(RunnerEvent.this.getPlugin(), 0L, 1L);
         } else if (this.time <= 0) {
            Player winner = RunnerEvent.this.getRandomPlayer();
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eRunner&6 event."));
               Bukkit.broadcastMessage("");
            }

            RunnerEvent.this.end();
            this.cancel();
            return;
         }

         if (RunnerEvent.this.getPlayers().size() == 1) {
            Player winner = Bukkit.getPlayer((UUID)RunnerEvent.this.getByState(RunnerPlayer.RunnerState.INGAME).get(0));
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eRunner&6 event."));
               Bukkit.broadcastMessage("");
            }

            RunnerEvent.this.end();
            this.cancel();
         } else {
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
               RunnerEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game ends in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
               RunnerEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game is ending in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
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
         RunnerEvent.this.getBukkitPlayers().forEach((player) -> {
            if (RunnerEvent.this.getPlayer(player.getUniqueId()) != null && ((RunnerPlayer)RunnerEvent.this.getPlayer(player.getUniqueId())).getState() == RunnerPlayer.RunnerState.INGAME) {
               if (RunnerEvent.this.getPlayers().size() <= 1) {
                  return;
               }

               if (RunnerEvent.this.getPlayers().containsKey(player.getUniqueId())) {
                  Block legs = player.getLocation().getBlock();
                  Block head = legs.getRelative(BlockFace.UP);
                  if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                     RunnerEvent.this.onDeath().accept(player);
                  }
               }

               Location loc = player.getLocation().clone().add((double)0.0F, (double)-1.0F, (double)0.0F);
               int y = loc.getBlockY();
               Block block = null;

               for(int i = 0; i <= 1; ++i) {
                  block = RunnerEvent.this.getBlockUnderPlayer(loc, y);
                  --y;
                  if (block != null) {
                     break;
                  }
               }

               if (RunnerEvent.this.getByState(RunnerPlayer.RunnerState.INGAME).size() == 1) {
                  return;
               }

               if (block != null) {
                  Block finalBlock = block;
                  Bukkit.getScheduler().runTaskLater(RunnerEvent.this.getPlugin(), () -> {
                     if (!RunnerEvent.this.getBlocks().containsKey(finalBlock.getLocation())) {
                        RunnerEvent.this.getBlocks().put(finalBlock.getLocation(), new ItemStack(finalBlock.getType(), 1, (short) finalBlock.getData()));
                     }

                     finalBlock.setType(Material.AIR);
                  }, 8L);
               }
            }

         });
      }

      public MoveTask() {
      }
   }

   private class PlayerPosition {
      private double x;
      private int y;
      private double z;

      public Block getBlock(World world, double addx, double addz) {
         return world.getBlockAt(NumberConversions.floor(this.x + addx), this.y, NumberConversions.floor(this.z + addz));
      }

      @ConstructorProperties({"x", "y", "z"})
      public PlayerPosition(double x, int y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }
}
