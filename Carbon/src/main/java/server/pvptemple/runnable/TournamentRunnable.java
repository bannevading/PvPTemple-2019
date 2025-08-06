package server.pvptemple.runnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.Carbon;
import server.pvptemple.kit.Kit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.queue.QueueType;
import server.pvptemple.tournament.Tournament;
import server.pvptemple.tournament.TournamentState;
import server.pvptemple.tournament.TournamentTeam;
import server.pvptemple.util.finalutil.CC;

public class TournamentRunnable extends BukkitRunnable {
   private final Carbon plugin = Carbon.getInstance();
   private final Tournament tournament;

   public void run() {
      if (this.tournament.getTournamentState() == TournamentState.STARTING) {
         int countdown = this.tournament.decrementCountdown();
         if (countdown == 0) {
            if (this.tournament.getCurrentRound() == 1) {
               Set<UUID> players = Sets.newConcurrentHashSet(this.tournament.getPlayers());

               for(UUID player : players) {
                  Party party = this.plugin.getPartyManager().getParty(player);
                  if (party != null) {
                     TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList(party.getMembers()));
                     this.tournament.addAliveTeam(team);

                     for(UUID member : party.getMembers()) {
                        players.remove(member);
                        this.tournament.setPlayerTeam(member, team);
                     }
                  }
               }

               List<UUID> currentTeam = null;

               for(UUID player : players) {
                  if (currentTeam == null) {
                     currentTeam = new ArrayList();
                  }

                  currentTeam.add(player);
                  if (currentTeam.size() == this.tournament.getTeamSize()) {
                     TournamentTeam team = new TournamentTeam((UUID)currentTeam.get(0), currentTeam);
                     this.tournament.addAliveTeam(team);

                     for(UUID teammate : team.getPlayers()) {
                        this.tournament.setPlayerTeam(teammate, team);
                     }

                     currentTeam = null;
                  }
               }
            }

            List<TournamentTeam> teams = this.tournament.getAliveTeams();
            Collections.shuffle(teams);

            for(int i = 0; i < teams.size(); i += 2) {
               TournamentTeam teamA = (TournamentTeam)teams.get(i);
               if (teams.size() <= i + 1) {
                  for(UUID playerUUID : teamA.getAlivePlayers()) {
                     Player player = this.plugin.getServer().getPlayer(playerUUID);
                     player.sendMessage(CC.PRIMARY + "You will be byed this round.");
                  }
               } else {
                  TournamentTeam teamB = (TournamentTeam)teams.get(i + 1);

                  for(UUID playerUUID : teamA.getAlivePlayers()) {
                     this.removeSpectator(playerUUID);
                  }

                  for(UUID playerUUID : teamB.getAlivePlayers()) {
                     this.removeSpectator(playerUUID);
                  }

                  MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList(teamA.getAlivePlayers()), (List)null, 0);
                  MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList(teamB.getAlivePlayers()), (List)null, 1);
                  Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());
                  Match match = new Match(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, new MatchTeam[]{matchTeamA, matchTeamB});
                  Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                  Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
                  match.broadcast("&eStarting &6" + kit.getName() + " &ematch between " + leaderA.getDisplayName() + " &eand " + leaderB.getDisplayName() + "&e.");
                  this.plugin.getMatchManager().createMatch(match);
                  this.tournament.addMatch(match.getMatchId());
                  this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), this.tournament.getId());
               }
            }

            this.tournament.broadcastWithSound("&3[Tournament] &eRound &6" + this.tournament.getCurrentRound() + " &ehas started!", Sound.FIREWORK_BLAST);
            this.tournament.broadcast("&3[Tournament] &eUse &6/tournament status " + this.tournament.getCurrentRound() + " &eto see who's fighting and to see the status of the tournament.");
            this.tournament.setTournamentState(TournamentState.FIGHTING);
         } else if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
            this.tournament.broadcastWithSound("&3[Tournament] &eRound &6" + this.tournament.getCurrentRound() + " &eis starting in &6" + countdown + "&e.", Sound.CLICK);
         }
      }

   }

   private void removeSpectator(UUID playerUUID) {
      Player player = this.plugin.getServer().getPlayer(playerUUID);
      if (player != null) {
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            this.plugin.getMatchManager().removeSpectator(player);
         }
      }

   }

   @ConstructorProperties({"tournament"})
   public TournamentRunnable(Tournament tournament) {
      this.tournament = tournament;
   }
}
