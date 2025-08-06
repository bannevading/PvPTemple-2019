package server.pvptemple.providers;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.board.Board;
import server.pvptemple.board.BoardAdapter;
import server.pvptemple.events.EventState;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.corners.FourCornersEvent;
import server.pvptemple.events.corners.FourCornersPlayer;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.lms.LMSPlayer;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.oitc.OITCPlayer;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.parkour.ParkourPlayer;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.runner.RunnerPlayer;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.events.sumo.SumoPlayer;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.queue.QueueEntry;
import server.pvptemple.queue.QueueType;
import server.pvptemple.tournament.Tournament;
import server.pvptemple.tournament.TournamentState;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class PracticeBoard implements BoardAdapter {
   private final Carbon plugin = Carbon.getInstance();
   private int fighters;
   private int queuing;

   public String getTitle(Player player) {
      return CC.B_PRIMARY + "PvPTemple " + CC.GRAY + "❘ " + CC.WHITE + this.plugin.getConfig().getString("REGION");
   }

   public void preLoop() {
      this.fighters = this.plugin.getMatchManager().getFighters() + (CorePlugin.getInstance().getPlayerManager().getDummyPlayers().size() > 1 ? CorePlugin.getInstance().getPlayerManager().getDummyPlayers().size() / 2 : 0);
      this.queuing = this.plugin.getQueueManager().getQueuing();
   }

   public List<String> getScoreboard(Player player, Board board) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData == null) {
         this.plugin.getLogger().warning(player.getName() + "'s player data is null");
         return null;
      } else if (!playerData.isScoreboardEnabled()) {
         return null;
      } else {
         switch (playerData.getPlayerState()) {
            case LOADING:
            case FFA:
            case SPAWN:
            case SPECTATING:
            case EDITING:
            case EVENT:
               return this.getLobbyBoard(player, false);
            case QUEUE:
               return this.getLobbyBoard(player, true);
            case FIGHTING:
               return this.getGameBoard(player);
            default:
               return null;
         }
      }
   }

   private List<String> getLobbyBoard(Player player, boolean queuing) {
      List<String> board = new LinkedList();
      board.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
      PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
      if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
         event = (PracticeEvent)this.plugin.getEventManager().getSpectators().get(player.getUniqueId());
      }

      if (event == null) {
         board.add(CC.GOLD + "Players: " + CC.WHITE + CorePlugin.getInstance().getServerPlayerCount());
         board.add(CC.GOLD + "Queuing: " + CC.WHITE + this.queuing);
         if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
            board.add(CC.GOLD + "Cooldown: " + CC.WHITE + formatTime(this.plugin.getEventManager().getCooldown() - System.currentTimeMillis()));
         }

         Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
         if (this.plugin.getTournamentManager().getTournaments().size() > 0) {
            Tournament tournament = this.plugin.getTournamentManager().getTournament(this.plugin.getTournamentManager().getLastCreatedId());
            if (tournament != null) {
               board.add("");
               board.add(CC.YELLOW + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName());
               board.add(CC.GRAY + (tournament.getTournamentState() != TournamentState.STARTING ? "Remaining: " : "Players: ") + CC.WHITE + tournament.getPlayers().size() + "/" + tournament.getSize());
               if (tournament.getTournamentState() != TournamentState.STARTING) {
                  board.add(CC.GRAY + "Round: " + CC.WHITE + tournament.getCurrentRound());
               }
            } else if (party != null) {
               board.add("");
               board.add("&eYour Party:");
               board.add("&7Leader: &f" + Bukkit.getPlayer(party.getLeader()).getName());
               board.add("&7Members: &f" + party.getMembers().size() + "/" + party.getLimit());
            }
         } else if (party != null) {
            board.add("");
            board.add("&eYour Party:");
            board.add("&7Leader: &f" + Bukkit.getPlayer(party.getLeader()).getName());
            board.add("&7Members: &f" + party.getMembers().size() + "/" + party.getLimit());
         }

         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (queuing) {
            board.add("");
            QueueEntry queueEntry = party == null ? this.plugin.getQueueManager().getQueueEntry(player.getUniqueId()) : this.plugin.getQueueManager().getQueueEntry(party.getLeader());
            board.add(CC.YELLOW + queueEntry.getQueueType().getName() + " " + queueEntry.getKitName());
            if (queueEntry.getQueueType() != QueueType.UNRANKED) {
               long queueTime = System.currentTimeMillis() - (party == null ? this.plugin.getQueueManager().getPlayerQueueTime(player.getUniqueId()) : this.plugin.getQueueManager().getPlayerQueueTime(party.getLeader()));
               int eloRange = playerData.getEloRange();
               int seconds = Math.round((float)(queueTime / 1000L));
               if (seconds > 5 && eloRange != -1) {
                  eloRange += seconds * 50;
                  if (eloRange >= 3000) {
                     eloRange = 3000;
                  }
               }

               int elo = playerData.getElo(queueEntry.getKitName());
               board.add(CC.GRAY + "Range: " + CC.WHITE + "[" + Math.max(elo - eloRange / 2, 0) + "-" + Math.max(elo + eloRange / 2, 0) + "]");
            }
         }
      } else {
         board.add("&6Event: &f" + event.getName());
         board.add("&6Host: &f" + event.getHost().getDisplayName());
         if (event instanceof SumoEvent) {
            SumoEvent sumoEvent = (SumoEvent)event;
            int playing = sumoEvent.getByState(SumoPlayer.SumoState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoState.PREPARING).size();
            board.add("");
            board.add("&6Players: &f" + playing + "/" + event.getLimit());
            board.add("");
            int countdown = sumoEvent.getCountdownTask().getTimeUntilStart();
            if (countdown > 0 && countdown <= 60) {
               board.add("&aStarting in " + this.niceInt(countdown) + "...");
            }

            if (sumoEvent.getFighting().size() > 0) {
               board.add("&eCurrent Match:");
               StringJoiner nameJoiner = new StringJoiner(" &7vs. &f");
               sumoEvent.getFighting().forEach(nameJoiner::add);
               board.add("&f" + nameJoiner.toString());
               StringJoiner pingJoiner = new StringJoiner(" &7vs. &f");
               sumoEvent.getFighting().forEach((p) -> pingJoiner.add(String.valueOf(Bukkit.getPlayer(p) == null ? "Offline" : PlayerUtil.getPing(Bukkit.getPlayer(p)) + " ms")));
               board.add("&f" + pingJoiner.toString());
            }
         } else if (event instanceof RunnerEvent) {
            RunnerEvent runnerEvent = (RunnerEvent)event;
            int playing = runnerEvent.getByState(RunnerPlayer.RunnerState.WAITING).size() + runnerEvent.getByState(RunnerPlayer.RunnerState.INGAME).size();
            board.add("");
            board.add("&6Players: &f" + playing + "/" + event.getLimit());
            int countdown = runnerEvent.getCountdownTask().getTimeUntilStart();
            if (countdown > 0 && countdown <= 60) {
               board.add("");
               board.add("&aStarting in " + this.niceInt(countdown) + "...");
            }
         } else if (event instanceof LMSEvent) {
            LMSEvent lmsEvent = (LMSEvent)event;
            int playing = lmsEvent.getByState(LMSPlayer.LMSState.WAITING).size() + lmsEvent.getByState(LMSPlayer.LMSState.FIGHTING).size();
            board.add("");
            board.add("&6Players: &f" + playing + "/" + event.getLimit());
            int countdown = lmsEvent.getCountdownTask().getTimeUntilStart();
            if (countdown > 0 && countdown <= 60) {
               board.add("");
               board.add("&aStarting in " + this.niceInt(countdown) + "...");
            }
         } else if (event instanceof ParkourEvent) {
            ParkourEvent parkourEvent = (ParkourEvent)event;
            int playing = parkourEvent.getByState(ParkourPlayer.ParkourState.WAITING).size() + parkourEvent.getByState(ParkourPlayer.ParkourState.INGAME).size();
            board.add("");
            board.add("&6Players: &f" + playing + "/" + event.getLimit());
            int countdown = parkourEvent.getCountdownTask().getTimeUntilStart();
            if (countdown > 0 && countdown <= 60) {
               board.add("");
               board.add("&aStarting in " + this.niceInt(countdown) + "...");
            }

            if (parkourEvent.getPlayer(player) != null) {
               ParkourPlayer parkourPlayer = (ParkourPlayer)parkourEvent.getPlayer(player);
               if (parkourPlayer.getLastCheckpoint() != null && parkourPlayer.getCheckpointId() > 0) {
                  board.add("&6Checkpoint: &f#" + parkourPlayer.getCheckpointId());
               }
            }
         } else if (event instanceof OITCEvent) {
            OITCEvent oitcEvent = (OITCEvent)event;
            int playing = oitcEvent.getPlayers().size();
            board.add("");
            board.add("&6Players: &f" + playing + "/" + event.getLimit());
            int countdown = oitcEvent.getCountdownTask().getTimeUntilStart();
            if (countdown > 0 && countdown <= 60) {
               board.add("");
               board.add("&aStarting in " + this.niceInt(countdown) + "...");
            }

            if (oitcEvent.getPlayer(player) != null) {
               OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(player);
               if (oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() == OITCPlayer.OITCState.RESPAWNING) {
                  board.add("&6Kills: &f" + oitcPlayer.getScore());
               }
            }

            List<OITCPlayer> sortedList = oitcEvent.sortedScores();
            if (sortedList.size() >= 2 && event.getState() == EventState.STARTED) {
               board.add("");
               board.add("&eTop Kills");
               Player first = Bukkit.getPlayer(((OITCPlayer)sortedList.get(0)).getUuid());
               Player second = Bukkit.getPlayer(((OITCPlayer)sortedList.get(1)).getUuid());
               if (first != null) {
                  board.add("&2#1: &a" + first.getName() + " &7(" + ((OITCPlayer)sortedList.get(0)).getScore() + ")");
               }

               if (second != null) {
                  board.add("&6#2: &e" + second.getName() + " &7(" + ((OITCPlayer)sortedList.get(1)).getScore() + ")");
               }

               if (sortedList.size() >= 3) {
                  Player third = Bukkit.getPlayer(((OITCPlayer)sortedList.get(2)).getUuid());
                  if (third != null) {
                     board.add("&3#3: &b" + third.getName() + " &7(" + ((OITCPlayer)sortedList.get(2)).getScore() + ")");
                  }
               }
            }
         } else if (event instanceof FourCornersEvent) {
            FourCornersEvent fourCornersEvent = (FourCornersEvent)event;
            int playing = fourCornersEvent.getByState(FourCornersPlayer.FourCornerState.WAITING).size() + fourCornersEvent.getByState(FourCornersPlayer.FourCornerState.INGAME).size();
            board.add("");
            board.add("&6Players: &f" + playing + "/" + event.getLimit());
            int countdown = fourCornersEvent.getCountdownTask().getTimeUntilStart();
            if (countdown > 0 && countdown <= 60) {
               board.add("");
               board.add("&aStarting in " + this.niceInt(countdown) + "...");
            }

            if (fourCornersEvent.getState().equals(EventState.STARTED)) {
               board.add("&6Round: &f#" + fourCornersEvent.getRound());
            }
         }
      }

      board.add("");
      board.add(CC.I_GRAY + "www.pvptemple.com");
      board.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
      return (List)board.stream().map(Color::translate).collect(Collectors.toList());
   }

   public void onScoreboardCreate(Player player, Scoreboard scoreboard) {
   }

   private List<String> getGameBoard(Player player) {
      List<String> board = new LinkedList();
      board.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
      Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
      board.add(CC.YELLOW + "Kit: " + CC.WHITE + match.getKit().getName());
      if (!match.isParty() && !match.isFFA()) {
         Player opponentPlayer = ((MatchTeam)match.getTeams().get(0)).getPlayers().get(0) == player.getUniqueId() ? this.plugin.getServer().getPlayer((UUID)((MatchTeam)match.getTeams().get(1)).getPlayers().get(0)) : this.plugin.getServer().getPlayer((UUID)((MatchTeam)match.getTeams().get(0)).getPlayers().get(0));
         if (opponentPlayer == null) {
            return this.getLobbyBoard(player, false);
         }

         board.add(CC.RED + "Opponent: " + CC.WHITE + opponentPlayer.getName());
         if (match.getType().isRanked()) {
            board.add("");
            board.add("&aYour Ping: &f" + PlayerUtil.getPing(player) + " ms");
            board.add("&cEnemy Ping: &f" + PlayerUtil.getPing(opponentPlayer) + " ms");
         }
      } else if (match.isParty() && !match.isFFA()) {
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         MatchTeam opposingTeam = match.isFFA() ? (MatchTeam)match.getTeams().get(0) : (playerData.getTeamID() == 0 ? (MatchTeam)match.getTeams().get(1) : (MatchTeam)match.getTeams().get(0));
         MatchTeam playerTeam = (MatchTeam)match.getTeams().get(playerData.getTeamID());
         board.add(CC.B_GRAY + " ");
         board.add(CC.GREEN + "Teammates: " + CC.WHITE + playerTeam.getAlivePlayers().size() + "/" + playerTeam.getPlayers().size());
         board.add(CC.RED + "Opponents: " + CC.WHITE + opposingTeam.getAlivePlayers().size() + "/" + opposingTeam.getPlayers().size());
      } else if (match.isFFA()) {
         board.add(CC.RED + "Remaining: " + CC.WHITE + ((MatchTeam)match.getTeams().get(0)).getAlivePlayers().size() + "/" + ((MatchTeam)match.getTeams().get(0)).getPlayers().size());
      }

      board.add("");
      board.add(CC.I_GRAY + "www.pvptemple.com");
      board.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
      return (List)board.stream().map(Color::translate).collect(Collectors.toList());
   }

   public long getInterval() {
      return 20L;
   }

   public static String formatTime(long millis) {
      int sec = (int)(millis / 1000L % 60L);
      int min = (int)(millis / 60000L % 60L);
      int hr = (int)(millis / 3600000L % 24L);
      return (hr > 0 ? String.format("%02d:", hr) : "") + String.format("%02d:%02d", min, sec);
   }

   private String niceInt(int i) {
      int r = i * 1000;
      int sec = r / 1000 % 60;
      int min = r / '\uea60' % 60;
      int h = r / 3600000 % 24;
      return (h > 0 ? (h < 10 ? "0" : "") + h + ":" : "") + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
   }
}
