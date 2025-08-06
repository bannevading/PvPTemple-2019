package server.pvptemple.commands.time;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.util.finalutil.CC;

public class NightCommand extends Command {
   public NightCommand() {
      super("night");
      this.setDescription("Set player time to night.");
      this.setUsage(CC.RED + "Usage: /night");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         ((Player)sender).getWorld().setTime(14000L);
         sender.sendMessage(CC.GREEN + "Time set to night.");
         return true;
      }
   }
}
