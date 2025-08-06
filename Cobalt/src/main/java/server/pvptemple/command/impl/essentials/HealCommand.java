package server.pvptemple.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class HealCommand extends Command {
   public HealCommand() {
      super("heal");
      this.setUsage(Color.translate("&cUsage: /heal <player>"));
      this.setDescription("Heal players.");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length == 0) {
            player.setHealth((double)20.0F);
            player.sendMessage(Color.translate("&6You have healed yourself."));
            return false;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            } else {
               target.setHealth((double)20.0F);
               Mineman pmineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
               String pcolor = pmineman.getCustomColor() != null && !pmineman.getCustomColor().isEmpty() && pmineman.hasRank(Rank.BASIC) ? pmineman.getCustomColor() : pmineman.getRank().getColor();
               target.sendMessage(Color.translate("&6You have been healed by " + pcolor + player.getName() + "&6."));
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
               String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
               player.sendMessage(Color.translate("&6You have healed " + color + target.getName() + "&6."));
               return false;
            }
         }
      }
   }
}
