package server.pvptemple.command.impl.essentials;

import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class SpawnerCommand extends Command {
   public SpawnerCommand() {
      super("spawner");
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
            player.sendMessage(Color.translate("&cUsage: /spawner <type>"));
            return false;
         } else {
            try {
               EntityType.valueOf(args[0].toUpperCase());
            } catch (Exception var8) {
               player.sendMessage(Color.translate("&cSpawner " + args[0] + " doesn't exists."));
               return false;
            }

            Block block = player.getTargetBlock((Set)null, 5);
            if (block != null && block.getType() == Material.MOB_SPAWNER) {
               EntityType entityType = EntityType.valueOf(args[0].toUpperCase());
               CreatureSpawner spawner = (CreatureSpawner)block.getState();
               spawner.setSpawnedType(entityType);
               spawner.update();
               player.sendMessage(Color.translate("&6You have updated spawner to &f" + entityType + "&6."));
               return false;
            } else {
               player.sendMessage(Color.translate("&cYou must be looking at spawner."));
               return false;
            }
         }
      }
   }
}
