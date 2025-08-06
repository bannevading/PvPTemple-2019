package server.pvptemple.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class PingCommand extends Command {
   public PingCommand() {
      super("ping");
      this.setDescription("Get ping of players.");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (args.length == 0) {
            player.sendMessage(Color.translate("&6Your ping: &f" + PlayerUtil.getPing(player) + " ms"));
            return false;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            } else {
               player.sendMessage(Color.translate(target.getDisplayName() + "&6's ping: &f" + PlayerUtil.getPing(target) + " ms"));
               return false;
            }
         }
      }
   }
}
