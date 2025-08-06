package server.pvptemple.commands.duel;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.corners.FourCornersEvent;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.StringUtil;

public class SpectateCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public SpectateCommand() {
      super("spec");
      this.setDescription("Spectate a player's match.");
      this.setUsage(CC.RED + "Usage: /spectate <player>");
      this.setAliases(Arrays.asList("sp", "spect", "spectate"));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
         } else {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
            if (party == null && (playerData.getPlayerState() == PlayerState.SPAWN || playerData.getPlayerState() == PlayerState.SPECTATING)) {
               Player target = this.plugin.getServer().getPlayer(args[0]);
               if (target == null) {
                  player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                  return true;
               } else {
                  PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                  if (targetData.getPlayerState() == PlayerState.EVENT) {
                     PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);
                     if (event == null) {
                        player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
                        return true;
                     } else {
                        if (event instanceof SumoEvent) {
                           player.performCommand("eventspectate Sumo");
                        } else if (event instanceof RunnerEvent) {
                           player.performCommand("eventspectate Runner");
                        } else if (event instanceof LMSEvent) {
                           player.performCommand("eventspectate LMS");
                        } else if (event instanceof ParkourEvent) {
                           player.performCommand("eventspectate Parkour");
                        } else if (event instanceof OITCEvent) {
                           player.performCommand("eventspectate OITC");
                        } else if (event instanceof FourCornersEvent) {
                           player.performCommand("eventspectate 4Corners");
                        }

                        return true;
                     }
                  } else if (targetData.getPlayerState() != PlayerState.FIGHTING) {
                     player.sendMessage(CC.RED + "Player is not in a match.");
                     return true;
                  } else {
                     Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
                     Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
                     if (!targetMatch.isParty() && !mineman.hasRank(Rank.TRAINEE)) {
                        if (!targetData.isAllowingSpectators()) {
                           player.sendMessage(CC.RED + "This player is not allowing spectators.");
                           return true;
                        }

                        MatchTeam team = (MatchTeam)targetMatch.getTeams().get(0);
                        MatchTeam team2 = (MatchTeam)targetMatch.getTeams().get(1);
                        PlayerData otherPlayerData = this.plugin.getPlayerManager().getPlayerData(team.getPlayers().get(0) == target.getUniqueId() ? (UUID)team2.getPlayers().get(0) : (UUID)team.getPlayers().get(0));
                        if (otherPlayerData != null && !otherPlayerData.isAllowingSpectators()) {
                           player.sendMessage(CC.RED + "The player this player is dueling is not allowing spectators.");
                           return true;
                        }
                     }

                     if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                        Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
                        if (match.equals(targetMatch)) {
                           player.sendMessage(CC.RED + "You are already spectating this match.");
                           return true;
                        }

                        match.removeSpectator(player.getUniqueId());
                     }

                     player.sendMessage(Color.translate("&aYou are now spectating " + target.getDisplayName() + "&a. &7(" + (targetMatch.getType().isRanked() ? "Ranked" : "Unranked") + " match)"));
                     player.sendMessage(CC.GOLD + "Now spectating: " + CC.WHITE + target.getName());
                     this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
                     return true;
                  }
               }
            } else {
               player.sendMessage(CC.RED + "You can't do this in your current state.");
               return true;
            }
         }
      }
   }
}
