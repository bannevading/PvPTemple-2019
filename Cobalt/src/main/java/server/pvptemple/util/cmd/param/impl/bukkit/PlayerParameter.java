package server.pvptemple.util.cmd.param.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;

public class PlayerParameter extends Parameter<Player> {
   public Player transfer(CommandSender sender, String value) {
      if (!(sender instanceof Player) || !value.equalsIgnoreCase("self") && !value.equals("")) {
         Player player = Bukkit.getPlayer(value);
         if (player == null) {
            sender.sendMessage(CC.RED + "No player with the name \"" + value + "\" found.");
            return null;
         } else {
            return player;
         }
      } else {
         return (Player)sender;
      }
   }
}
