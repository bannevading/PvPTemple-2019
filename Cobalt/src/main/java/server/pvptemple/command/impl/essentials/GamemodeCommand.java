package server.pvptemple.command.impl.essentials;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class GamemodeCommand extends Command {
   public GamemodeCommand() {
      super("gamemode");
      this.setAliases(Collections.singletonList("gm"));
      this.setUsage(Color.translate("&cUsage: /gamemode <c|s> <player>"));
      this.setDescription("Gamemode commands.");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
         sender.sendMessage(Color.translate("&cNo permission."));
         return false;
      } else if (args.length == 0) {
         sender.sendMessage(this.usageMessage);
         return false;
      } else {
         Player player = (Player)sender;
         if (!args[0].equalsIgnoreCase("c") && !args[0].equalsIgnoreCase("1") && !args[0].equalsIgnoreCase("creative")) {
            if (args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
               if (args.length == 1) {
                  if (sender instanceof ConsoleCommandSender) {
                     sender.sendMessage(this.usageMessage);
                  } else {
                     player.setGameMode(GameMode.SURVIVAL);
                     player.sendMessage(Color.translate("&6You are now in &fSURVIVAL &6mode."));
                  }

                  return false;
               }

               Player target = Bukkit.getPlayer(args[1]);
               if (target == null) {
                  sender.sendMessage(Color.translate("&cFailed to find that player."));
                  return false;
               }

               target.setGameMode(GameMode.SURVIVAL);
               target.sendMessage(Color.translate("&6You are now in &fSURVIVAL &6mode."));
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
               String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
               player.sendMessage(Color.translate(color + target.getName() + " &6is now in &fSURVIVAL &6mode."));
            }
         } else {
            if (args.length == 1) {
               if (sender instanceof ConsoleCommandSender) {
                  sender.sendMessage(this.usageMessage);
               } else {
                  player.setGameMode(GameMode.CREATIVE);
                  player.sendMessage(Color.translate("&6You are now in &fCREATIVE &6mode."));
               }

               return false;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
               sender.sendMessage(Color.translate("&cFailed to find that player."));
               return false;
            }

            target.setGameMode(GameMode.CREATIVE);
            target.sendMessage(Color.translate("&6You are now in &fCREATIVE &6mode."));
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
            String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
            player.sendMessage(Color.translate(color + target.getName() + " &6is now in &fCREATIVE &6mode."));
         }

         return false;
      }
   }
}
