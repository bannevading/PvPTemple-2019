package server.pvptemple.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class WorldCommand extends Command {
   public WorldCommand() {
      super("world");
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         return false;
      } else {
         Player player = (Player)sender;
         if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
         } else if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /world <world>"));
            this.sendWorlds(player);
            return false;
         } else {
            World world = Bukkit.getWorld(args[0]);
            if (world == null) {
               player.sendMessage(Color.translate("&cFailed to find that world."));
               this.sendWorlds(player);
               return false;
            } else {
               player.teleport(world.getSpawnLocation() != null ? world.getSpawnLocation() : new Location(world, (double)0.0F, (double)100.0F, (double)0.0F));
               player.sendMessage(Color.translate("&6Teleporting you to &f" + world.getName() + " &6world."));
               return false;
            }
         }
      }
   }

   private void sendWorlds(Player player) {
      StringBuilder builder = new StringBuilder();
      Bukkit.getWorlds().forEach((world) -> {
         if (builder.length() > 0) {
            builder.append(", ");
         }

         builder.append(world.getName());
      });
      player.sendMessage(Color.translate("&cAvailable worlds: " + builder.toString()));
   }
}
