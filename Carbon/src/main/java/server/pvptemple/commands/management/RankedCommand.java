package server.pvptemple.commands.management;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class RankedCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public RankedCommand() {
      super("ranked");
      this.setDescription("Manage server ranked mode.");
      this.setUsage(CC.RED + "Usage: /ranked");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (sender instanceof Player && PlayerUtil.testPermission(sender, Rank.ADMIN)) {
         boolean enabled = this.plugin.getQueueManager().isRankedEnabled();
         this.plugin.getQueueManager().setRankedEnabled(!enabled);
         sender.sendMessage(CC.GREEN + "Ranked matches are now " + (!enabled ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.GREEN + ".");
         return true;
      } else {
         return true;
      }
   }
}
