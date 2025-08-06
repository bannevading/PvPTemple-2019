package server.pvptemple.command.impl.essentials;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class SurvivalCommand extends Command {
   public SurvivalCommand() {
      super("survival");
      this.setAliases(Collections.singletonList("gms"));
      this.setUsage(Color.translate("&cUsage: /gms <player>"));
      this.setDescription("Set your gamemode to survival.");
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
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Color.translate("&6You are now in &fSURVIVAL &6mode."));
            return false;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            } else {
               target.setGameMode(GameMode.SURVIVAL);
               target.sendMessage(Color.translate("&6You are now in &fSURVIVAL &6mode."));
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
               String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
               player.sendMessage(Color.translate(color + target.getName() + " &6is now in &fSURVIVAL &6mode."));
               return false;
            }
         }
      }
   }
}
