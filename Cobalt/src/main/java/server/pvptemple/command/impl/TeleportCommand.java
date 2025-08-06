package server.pvptemple.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;

public class TeleportCommand implements CommandHandler {
   @Command(
      name = {"tpall", "teleportall"},
      rank = Rank.PLATFORMADMIN
   )
   public void teleportAllCommand(Player player) {
      if (!CorePlugin.getInstance().getServerManager().getServerName().contains("uhc")) {
         player.sendMessage(Color.translate("&cThis commmand can only be used on UHC servers!"));
      } else {
         Bukkit.getOnlinePlayers().stream().filter((o) -> !o.equals(player)).forEach((o) -> o.teleport(player));
         player.sendMessage(Color.translate("&6Teleported all online players to yourself."));
      }
   }

   @Command(
      name = {"tp", "goto", "tpto"},
      rank = Rank.TRAINEE
   )
   public void teleportCommand(Player player, @Param(name = "player") String playerName) {
      Player target = Bukkit.getPlayer(playerName);
      if (target == null) {
         player.sendMessage(CC.RED + "Invalid player.");
      } else {
         player.teleport(target.getLocation());
         player.sendMessage(Color.translate("&6Teleported you to " + target.getDisplayName() + "."));
      }
   }

   @Command(
      name = {"tphere"},
      rank = Rank.MOD
   )
   public void teleportHereCommand(Player player, @Param(name = "player") String playerName) {
      Player target = Bukkit.getPlayer(playerName);
      if (target == null) {
         player.sendMessage(CC.RED + "Invalid player.");
      } else {
         player.sendMessage(Color.translate("&6Teleporting " + target.getDisplayName() + " &6to you."));
         target.teleport(player.getLocation());
         target.sendMessage(Color.translate(player.getDisplayName() + " &6teleported you."));
      }
   }

   @Command(
      name = {"tppos"},
      rank = Rank.MOD
   )
   public void teleportPosCommand(Player player, @Param(name = "x") String x, @Param(name = "y") String y, @Param(name = "z") String z) {
      int x1;
      int y1;
      int z1;
      try {
         x1 = Integer.parseInt(x);
         y1 = Integer.parseInt(y);
         z1 = Integer.parseInt(z);
      } catch (Exception var9) {
         return;
      }

      player.teleport(new Location(player.getWorld(), (double)x1, (double)y1, (double)z1, 0.0F, 0.0F));
   }
}
