package server.pvptemple.commands;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.events.EventState;
import server.pvptemple.kit.Kit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.rank.Rank;
import server.pvptemple.tournament.Tournament;
import server.pvptemple.tournament.TournamentState;
import server.pvptemple.util.Color;
import server.pvptemple.util.TeamUtil;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class TournamentCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public TournamentCommand() {
      super("tournament");
      this.setUsage(CC.RED + "Usage: /tournament [args]");
   }

   public boolean execute(CommandSender commandSender, String s, String[] args) {
      if (args.length == 0) {
         this.sendUsage(commandSender);
         return true;
      } else if (!(commandSender instanceof Player)) {
         commandSender.sendMessage(CC.RED + "nigger");
         return true;
      } else {
         Player player = (Player)commandSender;
         if (!this.plugin.getEventManager().isEnabled()) {
            player.sendMessage(Color.translate("&cEvents are currently disabled. Please try again later!"));
            return false;
         } else {
            if (player.isOp() && this.plugin.getEventManager().getCooldown() > 0L) {
               this.plugin.getEventManager().setCooldown(0L);
            }

            if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
               player.sendMessage(Color.translate("&cThere is a event cooldown."));
               return false;
            } else {
               boolean eventBeingHosted = this.plugin.getEventManager().getEvents().values().stream().anyMatch((ex) -> ex.getState() != EventState.UNANNOUNCED);
               if (eventBeingHosted) {
                  player.sendMessage(Color.translate("&cAn event is already being hosted."));
                  return false;
               } else {
                  Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
                  switch (args[0].toLowerCase()) {
                     case "create":
                        if (mineman.hasRank(Rank.SENIORADMIN)) {
                           if (args.length == 5) {
                              try {
                                 int id = Integer.parseInt(args[1]);
                                 int teamSize = Integer.parseInt(args[3]);
                                 int size = Integer.parseInt(args[4]);
                                 String kitName = args[2];
                                 if (size % teamSize != 0) {
                                    commandSender.sendMessage(CC.RED + "This tournament size and team size would not work together.");
                                    return true;
                                 }

                                 if (this.plugin.getTournamentManager().getTournament(id) != null) {
                                    commandSender.sendMessage(CC.RED + "This tournament already exists.");
                                    return true;
                                 }

                                 Kit kit = this.plugin.getKitManager().getKit(kitName);
                                 if (kit == null) {
                                    commandSender.sendMessage(CC.RED + "This kit does not exist!");
                                    return true;
                                 }

                                 this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
                              } catch (NumberFormatException var21) {
                                 commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
                              }
                           } else {
                              commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
                           }
                        } else if (mineman.hasRank(Rank.MASTER)) {
                           long delta = System.currentTimeMillis() - this.plugin.getLastTournamentHostTime();
                           if (delta <= TimeUnit.MINUTES.toMillis(15L)) {
                              commandSender.sendMessage(CC.RED + "You must wait to host another tournament.");
                           }

                           if (args.length == 2) {
                              String kitName = args[1];
                              if (this.plugin.getTournamentManager().getTournament(1) != null) {
                                 commandSender.sendMessage(CC.RED + "There's already an active tournament.");
                                 return true;
                              }

                              Kit kit = this.plugin.getKitManager().getKit(kitName);
                              if (kit == null) {
                                 commandSender.sendMessage(CC.RED + "This kit does not exist!");
                                 return true;
                              }

                              this.plugin.getTournamentManager().createTournament(commandSender, 1, 1, 32, kitName);
                           } else {
                              commandSender.sendMessage(CC.RED + "Usage: /tournament create <kit>");
                           }
                        } else {
                           commandSender.sendMessage(CC.RED + "Insufficient permissions.");
                        }
                        break;
                     case "list":
                        commandSender.sendMessage(CC.GOLD + "Active Tournament Matches:");
                        StringBuilder builder2 = new StringBuilder();
                        this.plugin.getTournamentManager().getTournaments().keySet().forEach((i) -> builder2.append(i).append(", "));
                        commandSender.sendMessage(builder2.toString().trim());
                        break;
                     case "remove":
                        if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
                           return true;
                        }

                        if (args.length == 2) {
                           int id = Integer.parseInt(args[1]);
                           Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                           if (tournament != null) {
                              this.plugin.getTournamentManager().removeTournament(id);
                              commandSender.sendMessage(CC.PRIMARY + "Successfully removed tournament " + CC.SECONDARY + id + CC.PRIMARY + ".");
                           } else {
                              commandSender.sendMessage(CC.RED + "This tournament does not exist.");
                           }
                        } else {
                           commandSender.sendMessage(CC.RED + "Usage: /tournament remove <id>");
                        }
                        break;
                     case "announce":
                        if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
                           return true;
                        }

                        if (args.length == 2) {
                           int id = Integer.parseInt(args[1]);
                           Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                           if (tournament != null) {
                              Clickable clickable = new Clickable(CC.D_AQUA + "[Tournament] " + CC.GOLD + commandSender.getName() + CC.YELLOW + " is hosting a " + CC.GOLD + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName() + CC.YELLOW + " tournament! " + CC.GRAY + "[Click Here]", CC.GREEN + "Click to join!", "/tournament join " + id);
                              this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getServer().getOnlinePlayers().forEach(clickable::sendToPlayer));
                           }
                        } else {
                           commandSender.sendMessage(CC.RED + "Usage: /tournament announce <id>");
                        }
                        break;
                     case "join":
                        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                        if (playerData.getPlayerState() != PlayerState.SPAWN) {
                           player.sendMessage(CC.RED + "You can't do that in this state.");
                           return true;
                        }

                        if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                           player.sendMessage(CC.RED + "You are already in a tournament!");
                           return true;
                        }

                        if (args.length == 2) {
                           try {
                              int id = Integer.parseInt(args[1]);
                              Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                              if (tournament != null) {
                                 if (tournament.getSize() > tournament.getPlayers().size()) {
                                    if ((tournament.getTournamentState() == TournamentState.WAITING || tournament.getTournamentState() == TournamentState.STARTING) && tournament.getCurrentRound() == 1) {
                                       this.plugin.getTournamentManager().joinTournament(id, player);
                                    } else {
                                       player.sendMessage(CC.RED + "This tournament has already started!");
                                    }
                                 } else {
                                    player.sendMessage(CC.RED + "This tournament is already full!");
                                 }
                              } else {
                                 player.sendMessage(CC.RED + "This tournament doesn't exist!");
                              }
                           } catch (NumberFormatException var23) {
                              player.sendMessage(CC.RED + "This is not a number!");
                           }
                        } else {
                           player.sendMessage(CC.RED + "Usage: /tournament join <id>");
                        }
                        break;
                     case "status":
                        if (args.length == 2) {
                           try {
                              int id = Integer.parseInt(args[1]);
                              Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                              if (tournament != null) {
                                 StringBuilder builder = new StringBuilder();
                                 builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                                 builder.append(CC.SECONDARY).append("Tournament ").append(tournament.getId()).append(CC.PRIMARY).append("'s matches:");
                                 builder.append(CC.RED).append(" ").append(CC.RED).append("\n");

                                 for(UUID matchUUID : tournament.getMatches()) {
                                    Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                                    MatchTeam teamA = (MatchTeam)match.getTeams().get(0);
                                    MatchTeam teamB = (MatchTeam)match.getTeams().get(1);
                                    String teamANames = TeamUtil.getNames(teamA);
                                    String teamBNames = TeamUtil.getNames(teamB);
                                    builder.append(teamANames).append(" vs. ").append(teamBNames).append("\n");
                                 }

                                 builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                                 builder.append(CC.PRIMARY).append("Round: ").append(CC.SECONDARY).append(tournament.getCurrentRound()).append("\n");
                                 builder.append(CC.PRIMARY).append("Players: ").append(CC.SECONDARY).append(tournament.getPlayers().size()).append("\n");
                                 builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                                 commandSender.sendMessage(builder.toString());
                              } else {
                                 commandSender.sendMessage(CC.RED + "This tournament does not exist!");
                              }
                           } catch (NumberFormatException var22) {
                              commandSender.sendMessage(CC.RED + "This is not a number!");
                           }
                        }
                        break;
                     default:
                        this.sendUsage(commandSender);
                  }

                  return false;
               }
            }
         }
      }
   }

   private void sendUsage(CommandSender sender) {
      String[] text = new String[]{"&7&m" + StringUtils.repeat("-", 45), "&6Tournament Help &7❘ &fAll commands for tournaments", "", "&eGeneral Commands:", "&f/tournament join <id> &7Joins a tournament", "&f/tournament create <kit> &7Start a tournament", "&f/tournament status <id> &7Gives you a status", "&f/tournament list &7Lists active tournaments", "&7&m" + StringUtils.repeat("-", 45)};
      Stream.of(text).forEach((message) -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
   }
}
