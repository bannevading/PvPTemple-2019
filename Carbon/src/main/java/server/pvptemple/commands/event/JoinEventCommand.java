package server.pvptemple.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.events.EventState;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class JoinEventCommand implements CommandHandler {
   private final Carbon plugin = Carbon.getInstance();

   @Command(
      name = {"joinevent"},
      rank = Rank.NORMAL,
      description = "Join an event."
   )
   public void joinEvent(Player player, @Param(name = "join") String eventName) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (this.plugin.getPartyManager().getParty(player.getUniqueId()) == null && playerData.getPlayerState() == PlayerState.SPAWN) {
         PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
         if (event == null) {
            player.sendMessage(ChatColor.RED + "That event doesn't exist.");
         } else if (event.getState() != EventState.WAITING) {
            player.sendMessage(ChatColor.RED + "That event is currently not available.");
         } else if (event.getPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in this event.");
         } else if (event.getPlayers().size() >= event.getLimit() && !PlayerUtil.testPermission(player, Rank.PRIME)) {
            player.sendMessage(ChatColor.RED + "Sorry! The event is already full.");
         } else {
            event.join(player);
         }
      } else {
         player.sendMessage(CC.RED + "You can't do this in your current state.");
      }
   }
}
