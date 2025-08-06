package server.pvptemple.commands.event;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.events.EventState;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class EventManagerCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public EventManagerCommand() {
      super("eventmanager");
      this.setDescription("Manage an event.");
      this.setUsage(CC.RED + "Usage: /eventmanager <start/end/status/cooldown> <event>");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(CC.RED + "No permission.");
            return true;
         } else if (args.length < 2) {
            player.sendMessage(this.usageMessage);
            return true;
         } else {
            String action = args[0];
            String eventName = args[1];
            if (this.plugin.getEventManager().getByName(eventName) == null) {
               player.sendMessage(CC.RED + "That event doesn't exist.");
               return true;
            } else {
               PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
               if (action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
                  event.getCountdownTask().setTimeUntilStart(5);
                  player.sendMessage(CC.RED + "Event was force started.");
               } else if (action.toUpperCase().equalsIgnoreCase("END") && event.getState() == EventState.STARTED) {
                  event.end();
                  player.sendMessage(CC.RED + "Event was cancelled.");
               } else if (action.toUpperCase().equalsIgnoreCase("STATUS")) {
                  String[] message = new String[]{CC.YELLOW + "Event: " + CC.WHITE + event.getName(), CC.YELLOW + "Host: " + CC.WHITE + (event.getHost() == null ? "Player Left" : event.getHost().getName()), CC.YELLOW + "Players: " + CC.WHITE + event.getPlayers().size() + "/" + event.getLimit(), CC.YELLOW + "State: " + CC.WHITE + event.getState().name()};
                  player.sendMessage(message);
               } else if (action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {
                  this.plugin.getEventManager().setCooldown(0L);
                  player.sendMessage(CC.RED + "oEvent cooldown was cancelled.");
               } else {
                  player.sendMessage(this.usageMessage);
               }

               return true;
            }
         }
      }
   }
}
