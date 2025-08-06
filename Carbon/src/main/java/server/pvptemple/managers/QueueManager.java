package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.arena.Arena;
import server.pvptemple.kit.Kit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.queue.QueueEntry;
import server.pvptemple.queue.QueueType;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;

public class QueueManager {
   private final Map<UUID, QueueEntry> queued = new ConcurrentHashMap();
   private final Map<UUID, Long> playerQueueTime = new HashMap();
   private final Carbon plugin = Carbon.getInstance();
   private boolean rankedEnabled = true;

   public QueueManager() {
      this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> this.queued.forEach((key, value) -> {
            if (value.isParty()) {
               this.findMatch(this.plugin.getPartyManager().getParty(key), value.getKitName(), value.getElo(), value.getQueueType());
            } else {
               this.findMatch(this.plugin.getServer().getPlayer(key), value.getKitName(), value.getElo(), value.getQueueType());
            }

         }), 20L, 20L);
   }

   public void addPlayerToQueue(Player player, PlayerData playerData, String kitName, QueueType type) {
      if (type != QueueType.UNRANKED && !this.rankedEnabled) {
         player.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
         player.closeInventory();
      } else {
         playerData.setPlayerState(PlayerState.QUEUE);
         int elo = type == QueueType.RANKED ? playerData.getElo(kitName) : 0;
         QueueEntry entry = new QueueEntry(type, kitName, elo, false);
         this.queued.put(playerData.getUniqueId(), entry);
         this.giveQueueItems(player);
         player.sendMessage(type != QueueType.UNRANKED ? CC.GREEN + "You were added to the " + CC.GREEN + type.getName() + " " + kitName + CC.GREEN + " queue with " + CC.GREEN + elo + CC.GREEN + " ELO." : CC.GREEN + "You were added to the " + CC.GREEN + "Unranked " + kitName + CC.GREEN + " queue.");
         this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
         if (!this.findMatch(player, kitName, elo, type) && type.isRanked()) {
            player.sendMessage(CC.YELLOW + "Searching in ELO range " + CC.WHITE + (playerData.getEloRange() == -1 ? "Unrestricted" : "[" + Math.max(elo - playerData.getEloRange() / 2, 0) + " -> " + Math.max(elo + playerData.getEloRange() / 2, 0) + "]"));
         }

      }
   }

   private void giveQueueItems(Player player) {
      player.closeInventory();
      player.getInventory().setContents(this.plugin.getItemManager().getQueueItems());
      player.updateInventory();
   }

   public QueueEntry getQueueEntry(UUID uuid) {
      return (QueueEntry)this.queued.get(uuid);
   }

   public long getPlayerQueueTime(UUID uuid) {
      return (Long)this.playerQueueTime.get(uuid);
   }

   public int getQueueSize(String ladder, QueueType type) {
      return (int)this.queued.entrySet().stream().filter((entry) -> ((QueueEntry)entry.getValue()).getQueueType() == type).filter((entry) -> ((QueueEntry)entry.getValue()).getKitName().equals(ladder)).count();
   }

   private boolean findMatch(Player player, String kitName, int elo, QueueType type) {
      long queueTime = System.currentTimeMillis() - (Long)this.playerQueueTime.get(player.getUniqueId());
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData == null) {
         this.plugin.getLogger().warning(player.getName() + "'s player data is null");
         return false;
      } else {
         Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
         int eloRange = playerData.getEloRange();
         int pingRange = mineman.hasRank(Rank.MASTER) ? playerData.getPingRange() : -1;
         int seconds = Math.round((float)(queueTime / 1000L));
         if (seconds > 5 && type != QueueType.UNRANKED) {
            if (pingRange != -1) {
               pingRange += (seconds - 5) * 25;
            }

            if (eloRange != -1) {
               eloRange += seconds * 50;
               if (eloRange >= 3000) {
                  eloRange = 3000;
               } else {
                  player.sendMessage(CC.YELLOW + "Searching in ELO range " + CC.WHITE + "[" + Math.max(elo - eloRange / 2, 0) + " -> " + Math.max(elo + eloRange / 2, 0) + "]");
               }
            }
         }

         if (eloRange == -1) {
            eloRange = Integer.MAX_VALUE;
         }

         if (pingRange == -1) {
            pingRange = Integer.MAX_VALUE;
         }

         int ping = 0;
         Iterator var13 = this.queued.keySet().iterator();

         Player opponentPlayer;
         while(true) {
            if (!var13.hasNext()) {
               return false;
            }

            UUID opponent = (UUID)var13.next();
            if (!opponent.equals(player.getUniqueId())) {
               QueueEntry queueEntry = (QueueEntry)this.queued.get(opponent);
               if (queueEntry.getKitName().equals(kitName) && queueEntry.getQueueType() == type && !queueEntry.isParty()) {
                  opponentPlayer = this.plugin.getServer().getPlayer(opponent);
                  int eloDiff = Math.abs(queueEntry.getElo() - elo);
                  PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
                  if (!type.isRanked()) {
                     break;
                  }

                  if (eloDiff <= eloRange) {
                     Mineman opponentMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(opponent);
                     long opponentQueueTime = System.currentTimeMillis() - (Long)this.playerQueueTime.get(opponentPlayer.getUniqueId());
                     int opponentEloRange = opponentData.getEloRange();
                     int opponentPingRange = opponentMineman.hasRank(Rank.MASTER) ? opponentData.getPingRange() : -1;
                     int opponentSeconds = Math.round((float)(opponentQueueTime / 1000L));
                     if (opponentSeconds > 5) {
                        if (opponentPingRange != -1) {
                           opponentPingRange += (opponentSeconds - 5) * 25;
                        }

                        if (opponentEloRange != -1) {
                           opponentEloRange += opponentSeconds * 50;
                           if (opponentEloRange >= 3000) {
                              opponentEloRange = 3000;
                           }
                        }
                     }

                     if (opponentEloRange == -1) {
                        opponentEloRange = Integer.MAX_VALUE;
                     }

                     if (opponentPingRange == -1) {
                        opponentPingRange = Integer.MAX_VALUE;
                     }

                     if (eloDiff <= opponentEloRange) {
                        int pingDiff = Math.abs(0 - ping);
                        if (type == QueueType.RANKED) {
                           if (pingDiff <= opponentPingRange && pingDiff <= pingRange) {
                              break;
                           }
                        } else if (type != QueueType.PREMIUM || pingDiff <= 50) {
                           break;
                        }
                     }
                  }
               }
            }
         }

         Kit kit = this.plugin.getKitManager().getKit(kitName);
         Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
         String playerFoundMatchMessage;
         String matchedFoundMatchMessage;
         if (type.isRanked()) {
            playerFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + opponentPlayer.getName() + " (" + ((QueueEntry)this.queued.get(opponentPlayer.getUniqueId())).getElo() + ")");
            matchedFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + player.getName() + " (" + elo + ")");
         } else {
            playerFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + opponentPlayer.getName());
            matchedFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + player.getName());
         }

         player.sendMessage(playerFoundMatchMessage);
         opponentPlayer.sendMessage(matchedFoundMatchMessage);
         MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), Collections.singletonList(this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getMinemanID()), 0);
         MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), Collections.singletonList(this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId()).getMinemanID()), 1);
         Match match = new Match(arena, kit, type, new MatchTeam[]{teamA, teamB});
         this.plugin.getMatchManager().createMatch(match);
         this.queued.remove(player.getUniqueId());
         this.queued.remove(opponentPlayer.getUniqueId());
         this.playerQueueTime.remove(player.getUniqueId());
         return true;
      }
   }

   public void removePlayerFromQueue(Player player) {
      QueueEntry entry = (QueueEntry)this.queued.get(player.getUniqueId());
      if (entry != null) {
         this.queued.remove(player.getUniqueId());
         this.plugin.getPlayerManager().sendToSpawnAndReset(player);
         player.sendMessage(CC.RED + "You were removed from the " + CC.RED + entry.getQueueType().getName() + " " + entry.getKitName() + CC.RED + " queue.");
      }
   }

   public void addPartyToQueue(Player leader, Party party, String kitName, QueueType type) {
      if (type.isRanked() && !this.rankedEnabled) {
         leader.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
         leader.closeInventory();
      } else if (party.getMembers().size() != 2) {
         leader.sendMessage(CC.RED + "You can only join the queue with 2 players in your party.");
         leader.closeInventory();
      } else {
         Stream<UUID> var10000 = party.getMembers().stream();
         PlayerManager var10001 = this.plugin.getPlayerManager();
         var10000.map(var10001::getPlayerData).forEach((member) -> member.setPlayerState(PlayerState.QUEUE));
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(leader.getUniqueId());
         int elo = type.isRanked() ? playerData.getPartyElo(kitName) : -1;
         this.queued.put(playerData.getUniqueId(), new QueueEntry(type, kitName, elo, true));
         this.giveQueueItems(leader);
         party.broadcast(type.isRanked() ? CC.GREEN + "Your party was added to the " + type.getName().toLowerCase() + " " + CC.GREEN + kitName + CC.GREEN + " queue with " + CC.GREEN + elo + CC.GREEN + " ELO." : CC.GREEN + "Your party was added to the unranked " + CC.GREEN + kitName + CC.GREEN + " queue.");
         this.playerQueueTime.put(party.getLeader(), System.currentTimeMillis());
         this.findMatch(party, kitName, elo, type);
      }

   }

   private void findMatch(Party partyA, String kitName, int elo, QueueType type) {
      long queueTime = System.currentTimeMillis() - (Long)this.playerQueueTime.get(partyA.getLeader());
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(partyA.getLeader());
      int eloRange = playerData.getEloRange();
      int seconds = Math.round((float)(queueTime / 1000L));
      if (seconds > 5 && type.isRanked()) {
         eloRange += seconds * 50;
         if (eloRange >= 1000) {
            eloRange = 1000;
         }

         partyA.broadcast(CC.YELLOW + "Searching in ELO range " + CC.WHITE + "[" + (elo - eloRange / 2) + " -> " + (elo + eloRange / 2) + "]");
      }

      int finalEloRange = eloRange;
      UUID opponent = this.queued.entrySet().stream().filter((entry) -> entry.getKey() != partyA.getLeader()).filter((entry) -> ((QueueEntry)entry.getValue()).isParty()).filter((entry) -> ((QueueEntry)entry.getValue()).getQueueType() == type).filter((entry) -> !type.isRanked() || Math.abs(entry.getValue().getElo() - elo) < finalEloRange).filter((entry) -> entry.getValue().getKitName().equals(kitName)).map(Map.Entry::getKey).findFirst().orElse(null);
      if (opponent != null) {
         Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
         Player leaderB = this.plugin.getServer().getPlayer(opponent);
         Party partyB = this.plugin.getPartyManager().getParty(opponent);
         Kit kit = this.plugin.getKitManager().getKit(kitName);
         Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
         String partyAFoundMatchMessage;
         String partyBFoundMatchMessage;
         if (type.isRanked()) {
            partyAFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + leaderB.getName() + "'s Party (" + ((QueueEntry)this.queued.get(leaderB.getUniqueId())).getElo() + " elo)");
            partyBFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + leaderA.getName() + "'s Party (" + elo + " elo)");
         } else {
            partyAFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + leaderB.getName() + "'s Party");
            partyBFoundMatchMessage = Color.translate("&eStarting &6" + kit.getName() + " &ematch... Opponent: &c" + leaderA.getName() + "'s Party");
         }

         partyA.broadcast(partyAFoundMatchMessage);
         partyB.broadcast(partyBFoundMatchMessage);
         List<UUID> playersA = new ArrayList(partyA.getMembers());
         List<UUID> playersB = new ArrayList(partyB.getMembers());
         List<Integer> playerIdsA = new ArrayList();
         List<Integer> playerIdsB = new ArrayList();
         playersA.forEach((uuid) -> playerIdsA.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
         playersB.forEach((uuid) -> playerIdsB.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
         MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, playerIdsA, 0);
         MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, playerIdsB, 1);
         Match match = new Match(arena, kit, type, new MatchTeam[]{teamA, teamB});
         this.plugin.getMatchManager().createMatch(match);
         this.queued.remove(partyA.getLeader());
         this.queued.remove(partyB.getLeader());
      }
   }

   public void removePartyFromQueue(Party party) {
      QueueEntry entry = this.queued.get(party.getLeader());
      this.queued.remove(party.getLeader());
      Stream<Player> var10000 = party.members();
      PlayerManager var10001 = this.plugin.getPlayerManager();
      var10000.forEach(var10001::sendToSpawnAndReset);
      String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";
      party.broadcast(CC.RED + "Your party was removed from the " + CC.RED + type + " " + entry.getKitName() + CC.RED + " queue.");
   }

   public int getQueuing() {
      return this.queued.keySet().size();
   }

   public boolean isRankedEnabled() {
      return this.rankedEnabled;
   }

   public void setRankedEnabled(boolean rankedEnabled) {
      this.rankedEnabled = rankedEnabled;
   }
}
