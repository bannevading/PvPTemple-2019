package server.pvptemple.commands.event;

import java.util.Collections;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.tournament.Tournament;
import server.pvptemple.util.finalutil.CC;

public class LeaveEventCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public LeaveEventCommand() {
      super("leave");
      this.setAliases(Collections.singletonList("leaveevent"));
      this.setDescription("Leave an event or tournament.");
      this.setUsage(CC.RED + "Usage: /leave");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
         boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
         if (inEvent) {
            this.leaveEvent(player);
         } else if (inTournament) {
            this.leaveTournament(player);
         } else {
            player.sendMessage(CC.RED + "There is nothing to leave.");
         }

         return true;
      }
   }

   private void leaveTournament(Player player) {
      Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());
      if (tournament != null) {
         this.plugin.getTournamentManager().leaveTournament(player);
      }

   }

   private void leaveEvent(Player player) {
      PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
      if (event == null) {
         player.sendMessage(CC.RED + "That event does not exist.");
      } else if (!this.plugin.getEventManager().isPlaying(player, event)) {
         player.sendMessage(CC.RED + "You are not in a event.");
      } else {
         event.leave(player);
      }
   }
}
