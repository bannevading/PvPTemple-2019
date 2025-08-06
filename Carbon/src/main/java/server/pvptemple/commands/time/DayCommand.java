package server.pvptemple.commands.time;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.util.finalutil.CC;

public class DayCommand extends Command {
   public DayCommand() {
      super("day");
      this.setDescription("Set player time to day.");
      this.setUsage(CC.RED + "Usage: /day");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         ((Player)sender).getWorld().setTime(0L);
         sender.sendMessage(CC.GREEN + "Time set to day.");
         return true;
      }
   }
}
