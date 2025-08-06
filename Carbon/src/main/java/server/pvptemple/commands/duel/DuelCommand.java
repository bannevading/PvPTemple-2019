package server.pvptemple.commands.duel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.StringUtil;

public class DuelCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public DuelCommand() {
      super("duel");
      this.setDescription("Duel a player.");
      this.setUsage(CC.RED + "Usage: /duel <player>");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
         } else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "You are in a tournament.");
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
               } else if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
                  player.sendMessage(CC.RED + "This player is in a tournament.");
                  return true;
               } else {
                  Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                  if ((party == null || !this.plugin.getPartyManager().isInParty(target.getUniqueId(), party)) && !player.getName().equals(target.getName())) {
                     if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You aren't the leader!");
                        return true;
                     } else {
                        PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                        if (targetData.getPlayerState() != PlayerState.SPAWN) {
                           player.sendMessage(CC.RED + "That player isn't in spawn.");
                           return true;
                        } else if (!targetData.isAcceptingDuels()) {
                           player.sendMessage(CC.RED + "That player isn't accepting duel requests.");
                           return true;
                        } else {
                           Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                           if (party == null && targetParty != null) {
                              player.sendMessage(CC.RED + "They are in a party!");
                              return true;
                           } else if (party != null && targetParty == null) {
                              player.sendMessage(CC.RED + "You are in a party!");
                              return true;
                           } else {
                              playerData.setDuelSelecting(target.getUniqueId());
                              player.openInventory(this.plugin.getInventoryManager().getDuelInventory().getCurrentPage());
                              return true;
                           }
                        }
                     }
                  } else {
                     player.sendMessage(CC.RED + "You can't duel yourself.");
                     return true;
                  }
               }
            }
         }
      }
   }
}
