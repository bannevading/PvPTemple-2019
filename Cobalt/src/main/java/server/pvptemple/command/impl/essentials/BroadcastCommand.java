package server.pvptemple.command.impl.essentials;

import java.util.Arrays;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class BroadcastCommand extends Command {
   public BroadcastCommand() {
      super("broadcast");
      this.setAliases(Arrays.asList("bc", "bcast", "broadc"));
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!PlayerUtil.testPermission(sender, Rank.SENIORADMIN)) {
         sender.sendMessage(Color.translate("&cNo permission."));
         return false;
      } else if (args.length == 0) {
         sender.sendMessage(Color.translate("&cUsage: /broadcast <message>"));
         return false;
      } else {
         StringBuilder message = new StringBuilder();
         Stream.of(args).forEach((arg) -> message.append(arg).append(" "));
         Bukkit.broadcastMessage(Color.translate(message.toString().replace("{nl}", "\n")));
         return false;
      }
   }
}
