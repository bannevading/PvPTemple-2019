package server.pvptemple.commands.management;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class EventsCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public EventsCommand() {
      super("events");
      this.setAliases(Arrays.asList("toggleevents", "togglee", "te"));
      this.setDescription("Manage server events.");
      this.setUsage(CC.RED + "Usage: /events");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (sender instanceof Player && PlayerUtil.testPermission(sender, Rank.PLATFORMADMIN)) {
         boolean enabled = this.plugin.getEventManager().isEnabled();
         this.plugin.getEventManager().setEnabled(!enabled);
         sender.sendMessage(CC.GREEN + "Events are now " + (!enabled ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.GREEN + ".");
         return true;
      } else {
         return true;
      }
   }
}
