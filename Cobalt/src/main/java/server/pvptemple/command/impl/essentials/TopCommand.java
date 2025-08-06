package server.pvptemple.command.impl.essentials;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class TopCommand extends Command {
   public TopCommand() {
      super("top");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
         sender.sendMessage(Color.translate("&cNo permission."));
         return false;
      } else {
         Player player = (Player)sender;
         Location l = player.getLocation();
         player.teleport(new Location(l.getWorld(), l.getX(), (double)l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()), l.getZ(), l.getYaw(), l.getPitch()));
         player.sendMessage(Color.translate("&6Teleporting you to the highest location."));
         return false;
      }
   }
}
