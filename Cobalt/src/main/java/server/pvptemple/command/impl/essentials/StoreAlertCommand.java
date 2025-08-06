package server.pvptemple.command.impl.essentials;

import java.util.stream.Stream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import server.pvptemple.CorePlugin;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class StoreAlertCommand extends Command {
   public StoreAlertCommand() {
      super("storealert");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!PlayerUtil.testPermission(sender, Rank.PLATFORMADMIN)) {
         sender.sendMessage(Color.translate("&cNo permission."));
         return false;
      } else if (args.length == 0) {
         sender.sendMessage(Color.translate("&cUsage: /storealert <message>"));
         return false;
      } else {
         StringBuilder message = new StringBuilder();
         Stream.of(args).forEach((arg) -> message.append(arg).append(" "));
         CorePlugin.getInstance().getCoreRedisManager().broadcastGlobally(Color.translate(message.toString().replace("{nl}", "\n")));
         return false;
      }
   }
}
