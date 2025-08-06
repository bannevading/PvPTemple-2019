package server.pvptemple.command.impl.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class SpeedCommand extends Command {
   public SpeedCommand() {
      super("speed");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /speed <fly|walk> <speed>"));
            return false;
         } else {
            int amount;
            switch (args[0].toLowerCase()) {
               case "f":
               case "fly":
                  if (!this.isInteger(args[1])) {
                     player.sendMessage(Color.translate("&cFailed to parse integer."));
                     return false;
                  }

                  amount = Integer.parseInt(args[1]);
                  if (amount < 1 || amount > 10) {
                     player.sendMessage(Color.translate("&cSpeed limit is 10."));
                     return false;
                  }

                  player.setFlySpeed((float)amount * 0.1F);
                  player.sendMessage(Color.translate("&6You have set your fly speed to &f" + amount + "&6."));
                  break;
               case "w":
               case "walk":
                  if (!this.isInteger(args[1])) {
                     player.sendMessage(Color.translate("&cFailed to parse integer."));
                     return false;
                  }

                  amount = Integer.parseInt(args[1]);
                  if (amount < 1 || amount > 10) {
                     player.sendMessage(Color.translate("&cSpeed limit is 10."));
                     return false;
                  }

                  player.setWalkSpeed((float)amount * 0.1F);
                  player.sendMessage(Color.translate("&6You have set your walk speed to &f" + amount + "&6."));
            }

            return false;
         }
      }
   }

   private boolean isInteger(String value) {
      try {
         Integer.parseInt(value);
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }
}
