package server.pvptemple.util.finalutil;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.Color;

public final class PlayerUtil {
   public static final Comparator<Player> VISIBLE_RANK_ORDER = (a, b) -> {
      Mineman minemanA = CorePlugin.getInstance().getPlayerManager().getPlayer(a.getUniqueId());
      Mineman minemanB = CorePlugin.getInstance().getPlayerManager().getPlayer(b.getUniqueId());
      return -minemanA.getDisplayRank().compareTo(minemanB.getDisplayRank());
   };

   public PlayerUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   public static void messageRank(String message) {
      messageRank(message, Rank.TRAINEE);
   }

   public static void messageRank(String message, Rank rank) {
      CorePlugin.getInstance().getServer().getConsoleSender().sendMessage(server.pvptemple.util.finalutil.Color.translate(message));

      for(Player player : CorePlugin.getInstance().getServer().getOnlinePlayers()) {
         Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
         if (mineman != null && mineman.hasRank(rank) && (!rank.hasRank(Rank.TRAINEE) || mineman.isCanSeeStaffMessages())) {
            player.sendMessage(Color.translate(message));
         }
      }

   }

   public static boolean testPermission(CommandSender sender, Rank requiredRank, Permission permission, boolean requiresOp) {
      if (requiresOp && !sender.isOp()) {
         return false;
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         if (permission != null) {
            return player.hasPermission(permission);
         } else {
            return testPermission(sender, requiredRank);
         }
      } else {
         return true;
      }
   }

   public static boolean testPermission(CommandSender sender, Rank requiredRank) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
         if (mineman == null || !mineman.hasRank(requiredRank)) {
            return false;
         }
      }

      return true;
   }

   public static int getPing(Player player) {
      int ping = ((CraftPlayer)player).getHandle().ping;
      if (ping == 0) {
         ping = ThreadLocalRandom.current().nextInt(75, 150);
      }

      return ping;
   }
}
