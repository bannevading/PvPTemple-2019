package server.pvptemple.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.party.Party;
import server.pvptemple.rank.Rank;
import server.pvptemple.runnable.TournamentRunnable;
import server.pvptemple.tournament.Tournament;
import server.pvptemple.tournament.TournamentState;
import server.pvptemple.tournament.TournamentTeam;
import server.pvptemple.util.TeamUtil;
import server.pvptemple.util.finalutil.CC;

public class TournamentManager {
   private final Carbon plugin = Carbon.getInstance();
   private final Map<UUID, Integer> players = new HashMap();
   private final Map<UUID, Integer> matches = new HashMap();
   private final Map<Integer, Tournament> tournaments = new HashMap();
   private int lastCreatedId = 0;

   public boolean isInTournament(UUID uuid) {
      return this.players.containsKey(uuid);
   }

   public Tournament getTournament(UUID uuid) {
      Integer id = (Integer)this.players.get(uuid);
      return id == null ? null : (Tournament)this.tournaments.get(id);
   }

   public Tournament getTournamentFromMatch(UUID uuid) {
      Integer id = (Integer)this.matches.get(uuid);
      return id == null ? null : (Tournament)this.tournaments.get(id);
   }

   public void createTournament(CommandSender commandSender, int id, int teamSize, int size, String kitName) {
      Tournament tournament = new Tournament(id, teamSize, size, kitName);
      this.lastCreatedId = id;
      this.tournaments.put(id, tournament);
      (new TournamentRunnable(tournament)).runTaskTimer(this.plugin, 20L, 20L);
      if (commandSender instanceof Player) {
         tournament.addPlayer(((Player)commandSender).getUniqueId());
      }

      Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
         if (tournament.getTournamentState().equals(TournamentState.WAITING)) {
            this.plugin.getTournamentManager().removeTournament(1);
         }

      }, 3600L);
   }

   private void playerLeft(Tournament tournament, Player player) {
      TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
      tournament.removePlayer(player.getUniqueId());
      player.sendMessage(CC.PRIMARY + "You left the tournament.");
      this.players.remove(player.getUniqueId());
      this.plugin.getPlayerManager().sendToSpawnAndReset(player);
      Bukkit.broadcastMessage(CC.DARK_AQUA + "[Tournament] " + CC.GOLD + player.getName() + CC.PRIMARY + " has left. " + CC.GRAY + "(" + CC.R + tournament.getPlayers().size() + "/" + tournament.getSize() + CC.GRAY + ")");
      if (team != null) {
         team.killPlayer(player.getUniqueId());
         if (team.getAlivePlayers().size() == 0) {
            tournament.killTeam(team);
            if (tournament.getAliveTeams().size() == 1) {
               TournamentTeam tournamentTeam = (TournamentTeam)tournament.getAliveTeams().get(0);
               String names = TeamUtil.getNames(tournamentTeam);
               this.plugin.getServer().broadcastMessage(names + " won the tournament!");
               this.plugin.setLastTournamentHostTime(System.currentTimeMillis());

               for(UUID playerUUID : tournamentTeam.getAlivePlayers()) {
                  this.players.remove(playerUUID);
                  Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                  this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
               }

               this.plugin.getTournamentManager().removeTournament(tournament.getId());
            }
         } else if (team.getLeader().equals(player.getUniqueId())) {
            team.setLeader((UUID)team.getAlivePlayers().get(0));
         }
      }

   }

   private void teamEliminated(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
      for(UUID playerUUID : losingTeam.getAlivePlayers()) {
         Player player = this.plugin.getServer().getPlayer(playerUUID);
         tournament.removePlayer(player.getUniqueId());
         player.sendMessage(" ");
         player.sendMessage(CC.YELLOW + "You have been eliminated from the tournament. Better luck next time!");
         Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
         if (!mineman.hasRank(Rank.MASTER)) {
            player.sendMessage(CC.GRAY + "Purchase the Master rank at shop.pvptemple.com to host events of your own.");
         }

         player.sendMessage(" ");
         this.players.remove(player.getUniqueId());
      }

      String word = losingTeam.getAlivePlayers().size() > 1 ? "have" : "has";
      Bukkit.broadcastMessage(CC.DARK_AQUA + "[Tournament] " + CC.RED + TeamUtil.getNames(losingTeam) + CC.YELLOW + " " + word + " been eliminated by " + CC.RED + TeamUtil.getNames(winnerTeam) + CC.YELLOW + ".");
   }

   public void leaveTournament(Player player) {
      Tournament tournament = this.getTournament(player.getUniqueId());
      if (tournament != null) {
         Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
         if (party != null && tournament.getTournamentState() != TournamentState.FIGHTING) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
               for(UUID memberUUID : party.getMembers()) {
                  Player member = this.plugin.getServer().getPlayer(memberUUID);
                  this.playerLeft(tournament, member);
               }
            } else {
               player.sendMessage(CC.RED + "You are not the leader of this party!");
            }
         } else {
            this.playerLeft(tournament, player);
         }

      }
   }

   private void playerJoined(Tournament tournament, Player player) {
      tournament.addPlayer(player.getUniqueId());
      this.players.put(player.getUniqueId(), tournament.getId());
      this.plugin.getPlayerManager().sendToSpawnAndReset(player);
      Bukkit.broadcastMessage(CC.DARK_AQUA + "[Tournament] " + CC.GOLD + player.getName() + CC.PRIMARY + " has joined. " + CC.GRAY + "(" + CC.R + tournament.getPlayers().size() + "/" + tournament.getSize() + CC.GRAY + ")");
   }

   public void joinTournament(Integer id, Player player) {
      Tournament tournament = (Tournament)this.tournaments.get(id);
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null) {
         if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            if (party.getMembers().size() + tournament.getPlayers().size() <= tournament.getSize()) {
               if (party.getMembers().size() == tournament.getTeamSize() && party.getMembers().size() != 1) {
                  for(UUID memberUUID : party.getMembers()) {
                     Player member = this.plugin.getServer().getPlayer(memberUUID);
                     this.playerJoined(tournament, member);
                  }
               } else {
                  player.sendMessage(CC.RED + "You are in a party that does not match this tournament's description!");
               }
            } else {
               player.sendMessage(CC.RED + "This tournament is full!");
            }
         } else {
            player.sendMessage(CC.RED + "You are not the leader of this party!");
         }
      } else {
         this.playerJoined(tournament, player);
      }

      if (tournament.getPlayers().size() == tournament.getSize()) {
         tournament.setTournamentState(TournamentState.STARTING);
      }

   }

   public Tournament getTournament(Integer id) {
      return (Tournament)this.tournaments.get(id);
   }

   public void removeTournament(Integer id) {
      Tournament tournament = (Tournament)this.tournaments.get(id);
      if (tournament != null) {
         Iterator<UUID> iterator = tournament.getPlayers().iterator();
         if (iterator.hasNext()) {
            Player player = Bukkit.getPlayer((UUID)iterator.next());
            if (player != null) {
               player.performCommand("leaveevent");
            }
         }

         this.tournaments.remove(id);
      }
   }

   public void addTournamentMatch(UUID matchId, Integer tournamentId) {
      this.matches.put(matchId, tournamentId);
   }

   public void removeTournamentMatch(Match match) {
      Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
      if (tournament != null) {
         tournament.removeMatch(match.getMatchId());
         this.matches.remove(match.getMatchId());
         MatchTeam losingTeam = match.getWinningTeamId() == 0 ? (MatchTeam)match.getTeams().get(1) : (MatchTeam)match.getTeams().get(0);
         TournamentTeam losingTournamentTeam = tournament.getPlayerTeam((UUID)losingTeam.getPlayers().get(0));
         tournament.killTeam(losingTournamentTeam);
         MatchTeam winningTeam = (MatchTeam)match.getTeams().get(match.getWinningTeamId());
         TournamentTeam winningTournamentTeam = tournament.getPlayerTeam((UUID)winningTeam.getAlivePlayers().get(0));
         this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);
         winningTournamentTeam.broadcast(CC.YELLOW + "Use " + CC.GOLD + "/tournament status " + tournament.getId() + CC.YELLOW + " to see the remaining matches of this round!");
         if (tournament.getMatches().size() == 0) {
            if (tournament.getAliveTeams().size() > 1) {
               tournament.setTournamentState(TournamentState.STARTING);
               tournament.setCurrentRound(tournament.getCurrentRound() + 1);
               tournament.setCountdown(16);
            } else {
               String names = TeamUtil.getNames(winningTournamentTeam);
               this.plugin.getServer().broadcastMessage(CC.B_GOLD + names + " won the tournament!");

               for(UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
                  this.players.remove(playerUUID);
                  Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                  this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
               }

               this.plugin.getTournamentManager().removeTournament(tournament.getId());
               this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + 180000L);
            }
         }

      }
   }

   public Map<Integer, Tournament> getTournaments() {
      return this.tournaments;
   }

   public int getLastCreatedId() {
      return this.lastCreatedId;
   }
}
