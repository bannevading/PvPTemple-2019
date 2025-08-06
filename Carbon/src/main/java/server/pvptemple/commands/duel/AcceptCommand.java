package server.pvptemple.commands.duel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.kit.Kit;
import server.pvptemple.managers.PartyManager;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchRequest;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.queue.QueueType;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.StringUtil;

public class AcceptCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public AcceptCommand() {
      super("accept");
      this.setDescription("Accept a player's duel.");
      this.setUsage(CC.RED + "Usage: /accept <player>");
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
            if (playerData.getPlayerState() != PlayerState.SPAWN) {
               player.sendMessage(CC.RED + "You can't do this in your current state.");
               return true;
            } else {
               Player target = this.plugin.getServer().getPlayer(args[0]);
               if (target == null) {
                  player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                  return true;
               } else if (player.getName().equals(target.getName())) {
                  player.sendMessage(CC.RED + "You can't duel yourself.");
                  return true;
               } else {
                  PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                  if (targetData.getPlayerState() != PlayerState.SPAWN) {
                     player.sendMessage(CC.RED + "Player is not in spawn.");
                     return true;
                  } else {
                     MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());
                     if (args.length > 1) {
                        Kit kit = this.plugin.getKitManager().getKit(args[1]);
                        if (kit != null) {
                           request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
                        }
                     }

                     if (request == null) {
                        player.sendMessage(CC.RED + "You don't have a match request from that player.");
                        return true;
                     } else {
                        if (request.getRequester().equals(target.getUniqueId())) {
                           List<UUID> playersA = new ArrayList();
                           List<UUID> playersB = new ArrayList();
                           List<Integer> playerIdsA = new ArrayList();
                           List<Integer> playerIdsB = new ArrayList();
                           PartyManager partyManager = this.plugin.getPartyManager();
                           Party party = partyManager.getParty(player.getUniqueId());
                           Party targetParty = partyManager.getParty(target.getUniqueId());
                           if (request.isParty()) {
                              if (party == null || targetParty == null || !partyManager.isLeader(target.getUniqueId()) || !partyManager.isLeader(target.getUniqueId())) {
                                 player.sendMessage(CC.RED + "Either you or that player is not a party leader.");
                                 return true;
                              }

                              playersA.addAll(party.getMembers());
                              playersB.addAll(targetParty.getMembers());
                              party.getMembers().forEach((uuid) -> playerIdsA.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
                              targetParty.getMembers().forEach((uuid) -> playerIdsB.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
                           } else {
                              if (party != null || targetParty != null) {
                                 player.sendMessage(CC.RED + "One of you are in a party.");
                                 return true;
                              }

                              playersA.add(player.getUniqueId());
                              playersB.add(target.getUniqueId());
                              playerIdsA.add(playerData.getMinemanID());
                              playerIdsB.add(targetData.getMinemanID());
                           }

                           Kit kit = this.plugin.getKitManager().getKit(request.getKitName());
                           MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, playerIdsA, 0);
                           MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, playerIdsB, 1);
                           Match match = new Match(request.getArena(), kit, QueueType.UNRANKED, new MatchTeam[]{teamA, teamB});
                           Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                           Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
                           match.broadcast("&eStarting &6" + request.getKitName() + " &ematch... " + leaderA.getDisplayName() + " &evs " + leaderB.getDisplayName() + "&e.");
                           this.plugin.getMatchManager().createMatch(match);
                        }

                        return true;
                     }
                  }
               }
            }
         }
      }
   }
}
