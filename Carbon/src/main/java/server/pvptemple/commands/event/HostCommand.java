package server.pvptemple.commands.event;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.events.EventState;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class HostCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public HostCommand() {
      super("host");
      this.setAliases(Arrays.asList("hostevent", "eventhost", "event"));
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (sender instanceof ConsoleCommandSender) {
         sender.sendMessage("no gay");
         return false;
      } else {
         Player player = (Player)sender;
         if (!this.plugin.getEventManager().isEnabled()) {
            player.sendMessage(Color.translate("&cEvents are currently disabled. Please try again later!"));
            return false;
         } else if (args.length == 0) {
            player.openInventory(this.plugin.getInventoryManager().getHostInventory().getCurrentPage());
            return false;
         } else if (!PlayerUtil.testPermission(player, Rank.PRIME)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else {
            String eventName = args[0];
            if ((eventName.equalsIgnoreCase("parkour") || eventName.equalsIgnoreCase("sumo")) && !PlayerUtil.testPermission(player, Rank.ELITE)) {
               player.sendMessage(Color.translate("&cNo permission."));
               return false;
            } else if (eventName.equalsIgnoreCase("oitc") && !PlayerUtil.testPermission(player, Rank.MASTER)) {
               player.sendMessage(Color.translate("&cNo permission."));
               return false;
            } else if (this.plugin.getEventManager().getByName(eventName) == null) {
               player.sendMessage(Color.translate("&c" + eventName + " doesn't exist."));
               return false;
            } else {
               if (player.isOp() && this.plugin.getEventManager().getCooldown() > 0L) {
                  this.plugin.getEventManager().setCooldown(0L);
               }

               if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
                  player.sendMessage(Color.translate("&cThere is a event cooldown."));
                  return false;
               } else {
                  PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
                  if (event.getState() != EventState.UNANNOUNCED) {
                     player.sendMessage(Color.translate("&cThis event is already in progress."));
                     return false;
                  } else if (this.plugin.getTournamentManager().getTournament(1) != null) {
                     player.sendMessage(Color.translate("&cAn event is already being hosted."));
                     return true;
                  } else {
                     boolean eventBeingHosted = this.plugin.getEventManager().getEvents().values().stream().anyMatch((e) -> e.getState() != EventState.UNANNOUNCED);
                     if (eventBeingHosted) {
                        player.sendMessage(Color.translate("&cAn event is already being hosted."));
                        return false;
                     } else {
                        Clickable message = new Clickable(Color.translate("&8[&9Event&8] " + player.getDisplayName() + CC.GOLD + " is hosting " + CC.YELLOW + event.getName() + CC.GOLD + "!" + CC.GRAY + " [Click Here]"), CC.GREEN + "Click to join!", "/joinevent " + event.getName());
                        Bukkit.getOnlinePlayers().forEach(message::sendToPlayer);
                        Carbon.getInstance().getEventManager().hostEvent(event, player);
                        return false;
                     }
                  }
               }
            }
         }
      }
   }
}
