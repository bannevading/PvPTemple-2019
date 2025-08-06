package server.pvptemple.util.cmd.param.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;

public class WorldParameter extends Parameter<World> {
   public World transfer(CommandSender sender, String argument) {
      World world = Bukkit.getWorld(argument);
      if (world == null) {
         sender.sendMessage(CC.RED + "World '" + argument + "' not found!");
         return null;
      } else {
         return world;
      }
   }
}
