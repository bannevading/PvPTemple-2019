package server.pvptemple.events.lms;

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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.EventPlayer;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.finalutil.CC;

public class LMSEvent extends PracticeEvent<LMSPlayer> {
   private final Map<UUID, LMSPlayer> players = new HashMap();
   private final LMSCountdownTask countdownTask = new LMSCountdownTask(this);
   private LMSGameTask gameTask;

   public LMSEvent() {
      super("LMS");
   }

   public Map<UUID, LMSPlayer> getPlayers() {
      return this.players;
   }

   public EventCountdownTask getCountdownTask() {
      return this.countdownTask;
   }

   public List<CustomLocation> getSpawnLocations() {
      return this.getPlugin().getSpawnManager().getLmsLocations();
   }

   public void onStart() {
      this.gameTask = new LMSGameTask();
      this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
   }

   public void cancelAll() {
      if (this.gameTask != null) {
         this.gameTask.cancel();
      }

   }

   public Consumer<Player> onJoin() {
      return (player) -> {
         LMSPlayer var10000 = (LMSPlayer)this.players.put(player.getUniqueId(), new LMSPlayer(player.getUniqueId(), this));
      };
   }

   public Consumer<Player> onDeath() {
      return (player) -> {
         LMSPlayer data = (LMSPlayer)this.getPlayer(player);
         if (data.getState() == LMSPlayer.LMSState.FIGHTING) {
            Player killer = player.getKiller();
            data.setState(LMSPlayer.LMSState.ELIMINATED);
            this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
               this.getPlugin().getPlayerManager().sendToSpawnAndReset(player);
               if (this.getPlayers().size() >= 2) {
                  this.getPlugin().getEventManager().addSpectatorLMS(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
               }

            });
            this.getPlayers().remove(player.getUniqueId());
            this.sendMessage("&8[&9Event&8] " + player.getDisplayName() + CC.GOLD + " was eliminated" + (killer == null ? "." : " by " + CC.YELLOW + killer.getName()) + CC.GOLD + ".");
            player.sendMessage(" ");
            player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && !mineman.hasRank(Rank.PRIME)) {
               player.sendMessage(CC.GRAY + "Purchase a rank at shop.pvptemple.com to host events of your own.");
            }

            player.sendMessage(" ");
            if (this.getByState(LMSPlayer.LMSState.FIGHTING).size() == 1) {
               Player winner = Bukkit.getPlayer((UUID)this.getByState(LMSPlayer.LMSState.FIGHTING).get(0));
               if (winner != null) {
                  Bukkit.broadcastMessage("");
                  Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eLMS&6 event."));
                  Bukkit.broadcastMessage("");
               }

               this.end();
               this.cancelAll();
            }

         }
      };
   }

   private Player getRandomPlayer() {
      if (this.getByState(LMSPlayer.LMSState.FIGHTING).size() == 0) {
         return null;
      } else {
         List<UUID> fighting = this.getByState(LMSPlayer.LMSState.FIGHTING);
         Collections.shuffle(fighting);
         UUID uuid = (UUID)fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));
         return this.getPlugin().getServer().getPlayer(uuid);
      }
   }

   public List<UUID> getByState(LMSPlayer.LMSState state) {
      return (List)this.players.values().stream().filter((player) -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
   }

   private void handleInventory(Player player) {
      Carbon.getInstance().getKitManager().getKit("Soup").applyToPlayer(player);
   }

   public LMSGameTask getGameTask() {
      return this.gameTask;
   }

   public class LMSGameTask extends BukkitRunnable {
      private int time = 303;

      public void run() {
         if (this.time == 303) {
            LMSEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "3 seconds" + CC.GOLD + ".");
         } else if (this.time == 302) {
            LMSEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "2 seconds" + CC.GOLD + ".");
         } else if (this.time == 301) {
            LMSEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Starting in " + CC.YELLOW + "1 second" + CC.GOLD + ".");
         } else if (this.time == 300) {
            LMSEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "The game has started.");
            LMSEvent.this.getPlayers().values().forEach((player) -> player.setState(LMSPlayer.LMSState.FIGHTING));
            LMSEvent.this.getBukkitPlayers().forEach((x$0) -> LMSEvent.this.handleInventory(x$0));
         } else if (this.time <= 0) {
            Player winner = LMSEvent.this.getRandomPlayer();
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eLMS&6 event."));
               Bukkit.broadcastMessage("");
            }

            LMSEvent.this.end();
            this.cancel();
            return;
         }

         if (LMSEvent.this.getPlayers().size() == 1) {
            Player winner = Bukkit.getPlayer((UUID)LMSEvent.this.getByState(LMSPlayer.LMSState.FIGHTING).get(0));
            if (winner != null) {
               Bukkit.broadcastMessage("");
               Bukkit.broadcastMessage(Color.translate("&8[&9Event&8] " + winner.getDisplayName() + CC.GOLD + " has won the &eLMS&6 event."));
               Bukkit.broadcastMessage("");
            }

            LMSEvent.this.end();
            this.cancel();
         } else {
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
               LMSEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game ends in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
               LMSEvent.this.sendMessage("&8[&9Event&8] " + CC.GOLD + "Game is ending in " + CC.YELLOW + this.time + " seconds" + CC.GOLD + ".");
            }

            --this.time;
         }
      }

      public int getTime() {
         return this.time;
      }
   }
}
