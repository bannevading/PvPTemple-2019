package server.pvptemple.commands.time;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.util.finalutil.CC;

public class SunsetCommand extends Command {
   public SunsetCommand() {
      super("sunset");
      this.setDescription("Set player time to sunset.");
      this.setUsage(CC.RED + "Usage: /sunset");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         ((Player)sender).getWorld().setTime(12000L);
         sender.sendMessage(CC.GREEN + "Time set to sunset.");
         return true;
      }
   }
}
