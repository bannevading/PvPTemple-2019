package server.pvptemple.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.StringUtil;

public class PartyCommand extends Command {
   private static final String NOT_LEADER;
   private static final String HELP_MESSAGE;
   private final Carbon plugin = Carbon.getInstance();

   public PartyCommand() {
      super("party");
      this.setDescription("Manager player parties.");
      this.setUsage(CC.RED + "Usage: /party <subcommand> [player]");
      this.setAliases(Collections.singletonList("p"));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
         String subCommand = args.length < 1 ? "help" : args[0];
         switch (subCommand.toLowerCase()) {
            case "create":
               if (party != null) {
                  player.sendMessage(CC.RED + "You are already in a party.");
               } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                  player.sendMessage(CC.RED + "You can't do this in your current state.");
               } else {
                  this.plugin.getPartyManager().createParty(player);
               }
               break;
            case "leave":
               if (party == null) {
                  player.sendMessage(CC.RED + "You are not in a party.");
               } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                  player.sendMessage(CC.RED + "You can't do this in your current state.");
               } else {
                  this.plugin.getPartyManager().leaveParty(player);
               }
               break;
            case "inv":
            case "invite":
               if (party == null) {
                  player.sendMessage(CC.RED + "You are not in a party.");
               } else if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                  player.sendMessage(CC.RED + "You are not the party leader.");
               } else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
                  player.sendMessage(CC.RED + "You are in a tournament.");
               } else if (args.length < 2) {
                  player.sendMessage(CC.RED + "Usage: /party invite <player>.");
               } else if (party.isOpen()) {
                  player.sendMessage(CC.GREEN + "The party is open, so anyone can join.");
               } else if (party.getMembers().size() >= party.getLimit()) {
                  player.sendMessage(CC.RED + "The party has reached its member limit.");
               } else {
                  if (party.getLeader() != player.getUniqueId()) {
                     player.sendMessage(NOT_LEADER);
                     return true;
                  }

                  Player target = this.plugin.getServer().getPlayer(args[1]);
                  if (target == null) {
                     player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                     return true;
                  }

                  PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                  if (target.getUniqueId() == player.getUniqueId()) {
                     player.sendMessage(CC.RED + "You can't invite yourself.");
                  } else if (this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
                     player.sendMessage(CC.RED + "That player is already in a party.");
                  } else if (targetData.getPlayerState() != PlayerState.SPAWN) {
                     player.sendMessage(CC.RED + "That player isn't in spawn.");
                  } else if (this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
                     player.sendMessage(CC.RED + "You already sent a party request to that player. Please wait until it expires.");
                  } else {
                     this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());
                     Clickable partyInvite = new Clickable(CC.WHITE + ((Player)sender).getDisplayName() + CC.GREEN + " has invited you to their party. " + CC.GRAY + "[Click Here]", CC.GREEN + "Click to accept", "/party accept " + sender.getName());
                     partyInvite.sendToPlayer(target);
                     party.broadcast(CC.RED + target.getName() + CC.YELLOW + " was invited to the party.");
                  }
               }
               break;
            case "accept":
               if (party != null) {
                  player.sendMessage(CC.RED + "You are already in a party.");
               } else if (args.length < 2) {
                  player.sendMessage(CC.RED + "Usage: /party accept <player>.");
               } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                  player.sendMessage(CC.RED + "You can't do this in your current state.");
               } else {
                  Player target = this.plugin.getServer().getPlayer(args[1]);
                  if (target == null) {
                     player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                     return true;
                  }

                  Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                  if (targetParty == null) {
                     player.sendMessage(CC.RED + "That player does not have a party.");
                  } else if (targetParty.getMembers().size() >= targetParty.getLimit()) {
                     player.sendMessage(CC.RED + "That party is full.");
                  } else if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
                     player.sendMessage(CC.RED + "You don't have an invite from that player.");
                  } else {
                     this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                  }
               }
               break;
            case "join":
               if (party != null) {
                  player.sendMessage(CC.RED + "You are already in a party.");
               } else if (args.length < 2) {
                  player.sendMessage(CC.RED + "Usage: /party join <player>.");
               } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                  player.sendMessage(CC.RED + "You can't do this in your current state.");
               } else {
                  Player target = this.plugin.getServer().getPlayer(args[1]);
                  if (target == null) {
                     player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                     return true;
                  }

                  Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                  if (targetParty != null && targetParty.isOpen() && targetParty.getMembers().size() < targetParty.getLimit()) {
                     this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                  } else {
                     player.sendMessage(CC.RED + "You can't join this party.");
                  }
               }
               break;
            case "kick":
               if (party == null) {
                  player.sendMessage(CC.RED + "You are not in a party.");
               } else if (args.length < 2) {
                  player.sendMessage(CC.RED + "Usage: /party kick <player>.");
               } else {
                  if (party.getLeader() != player.getUniqueId()) {
                     player.sendMessage(NOT_LEADER);
                     return true;
                  }

                  Player target = this.plugin.getServer().getPlayer(args[1]);
                  if (target == null) {
                     player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                     return true;
                  }

                  Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                  if (targetParty != null && targetParty.getLeader() == party.getLeader()) {
                     this.plugin.getPartyManager().leaveParty(target);
                     target.sendMessage(CC.RED + "You were kicked from the party.");
                  } else {
                     player.sendMessage(CC.RED + "That player is not in your party.");
                  }
               }
               break;
            case "limit":
               if (party == null) {
                  player.sendMessage(CC.RED + "You are not in a party.");
               } else if (args.length < 2) {
                  player.sendMessage(CC.RED + "Usage: /party kick <player>.");
               } else {
                  if (party.getLeader() != player.getUniqueId()) {
                     player.sendMessage(NOT_LEADER);
                     return true;
                  }

                  try {
                     int limit = Integer.parseInt(args[1]);
                     if (limit >= 2 && limit <= 300) {
                        party.setLimit(limit);
                        player.sendMessage(CC.PRIMARY + "Your party's limit is now " + CC.SECONDARY + limit + CC.PRIMARY + " members.");
                     } else {
                        player.sendMessage(CC.RED + "That is not a valid limit.");
                     }
                  } catch (NumberFormatException var13) {
                     player.sendMessage(CC.RED + "That is not a valid limit.");
                  }
               }
               break;
            case "open":
            case "close":
               if (party == null) {
                  player.sendMessage(CC.RED + "You are not in a party.");
               } else {
                  if (party.getLeader() != player.getUniqueId()) {
                     player.sendMessage(NOT_LEADER);
                     return true;
                  }

                  party.setOpen(!party.isOpen());
                  party.broadcast(CC.YELLOW + "Your party is now " + CC.SECONDARY + (party.isOpen() ? "open" : "closed") + CC.PRIMARY + ".");
               }
               break;
            case "list":
               if (party == null) {
                  player.sendMessage(CC.RED + "You are not in a party.");
                  return false;
               }

               List<UUID> members = new ArrayList(party.getMembers());
               StringBuilder builder = new StringBuilder();
               members.forEach((member) -> {
                  if (builder.length() > 0) {
                     builder.append("&7, ");
                  }

                  Player pmember = Bukkit.getPlayer(member);
                  builder.append("&a").append(pmember.getName());
               });
               player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
               player.sendMessage(Color.translate("&6" + Bukkit.getPlayer(party.getLeader()).getName() + "'s Party &7[" + party.getMembers().size() + "/" + party.getLimit() + "]"));
               player.sendMessage(Color.translate("&eState: &f" + (party.isOpen() ? "Open" : "Locked")));
               player.sendMessage(Color.translate("&eMembers: " + builder.toString() + "&7."));
               player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
               break;
            default:
               this.sendUsage(player);
         }

         return true;
      }
   }

   private void sendUsage(Player sender) {
      String[] text = new String[]{"&7&m" + StringUtils.repeat("-", 45), "&6Party Help &7❘ &fAll commands for parties", "", "&eGeneral Commands:", "&f/p join <player> &7Joins an open party", "&f/p accept <player> &7Accepts a party invite", "&f/p leave &7Leaves a party", "&f/p create &7Creates a new party", "", "&eInformation Commands:", "&f/p list &7Display party information", "", "&eLeader Commands:", "&f/p invite <player> &7Invites a player", "&f/p limit <limit> &7Sets a player limit", "&f/p kick <player> &7Kicks a player", "&f/p open &7Opens the party", "&f/p close &7Closes a party", "&7&m" + StringUtils.repeat("-", 45)};
      Stream.of(text).forEach((message) -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
   }

   static {
      NOT_LEADER = CC.RED + "You are not the leader of the party!";
      HELP_MESSAGE = StringUtil.center(CC.GRAY + CC.STRIKE_THROUGH + "---------------------------------------------") + StringUtil.center(CC.GOLD + "Party Help\n") + StringUtil.center(CC.WHITE + "/party create" + CC.GRAY + " Creates a party\n") + StringUtil.center(CC.WHITE + "/party leave" + CC.GRAY + " Leaves or disbands\n") + StringUtil.center(CC.WHITE + "/party invite <player>" + CC.GRAY + " Invites a player\n") + StringUtil.center(CC.WHITE + "/party accept <player>" + CC.GRAY + " Accepts an invite\n") + StringUtil.center(CC.WHITE + "/party join <player>" + CC.GRAY + " Joins an open party\n") + StringUtil.center(CC.WHITE + "/party kick <player>" + CC.GRAY + " Kicks a player\n") + StringUtil.center(CC.WHITE + "/party limit <limit>" + CC.GRAY + " Sets a player limit\n") + StringUtil.center(CC.WHITE + "/party open" + CC.GRAY + " Opens a party\n") + StringUtil.center(CC.WHITE + "/party close" + CC.GRAY + " Closes a party\n") + StringUtil.center(CC.WHITE + "/party list" + CC.GRAY + " Lists the party members") + StringUtil.center(CC.GRAY + CC.STRIKE_THROUGH + "---------------------------------------------");
   }
}
