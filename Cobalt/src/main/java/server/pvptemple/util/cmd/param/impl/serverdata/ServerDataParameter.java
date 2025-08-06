package server.pvptemple.util.cmd.param.impl.serverdata;

import org.bukkit.command.CommandSender;
import server.pvptemple.CorePlugin;
import server.pvptemple.server.ServerData;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.cmd.param.impl.serverdata.WrappedServerData;
import server.pvptemple.util.finalutil.CC;

public class ServerDataParameter extends Parameter<server.pvptemple.util.cmd.param.impl.serverdata.WrappedServerData> {
   public server.pvptemple.util.cmd.param.impl.serverdata.WrappedServerData transfer(CommandSender sender, String argument) {
      if (!argument.equalsIgnoreCase("self") && !argument.equalsIgnoreCase("this") && !argument.equalsIgnoreCase("")) {
         ServerData serverData = CorePlugin.getInstance().getServerManager().getServerDataByName(argument);
         if (serverData == null) {
            sender.sendMessage(CC.RED + "Server '" + argument + "' not found!");
            return null;
         } else {
            return new server.pvptemple.util.cmd.param.impl.serverdata.WrappedServerData(argument, serverData);
         }
      } else {
         return new WrappedServerData(CorePlugin.getInstance().getServerManager().getServerName(), (ServerData)CorePlugin.getInstance().getServerManager().getServers().get(CorePlugin.getInstance().getServerManager().getServerName()));
      }
   }
}
