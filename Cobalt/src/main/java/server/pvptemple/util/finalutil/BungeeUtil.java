package server.pvptemple.util.finalutil;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.event.bungee.BungeeConnectEvent;

public final class BungeeUtil {
   private BungeeUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   public static void sendMessage(Player source, String target, String message) {
      Validate.notNull(source, target, message, "Input values cannot be null!");

      try {
         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("Message");
         out.writeUTF(target);
         out.writeUTF(message);
         source.sendPluginMessage(CorePlugin.getInstance(), "BungeeCord", out.toByteArray());
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static void kickPlayer(Player source, String target, String reason) {
      Validate.notNull(source, target, reason, "Input values cannot be null!");

      try {
         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("KickPlayer");
         out.writeUTF(target);
         out.writeUTF(reason);
         source.sendPluginMessage(CorePlugin.getInstance(), "BungeeCord", out.toByteArray());
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static void sendToServer(Player player, String server) {
      Validate.notNull(player, server, "Input values cannot be null!");

      try {
         BungeeConnectEvent bungeeConnectEvent = new BungeeConnectEvent(player, server);
         Bukkit.getServer().getPluginManager().callEvent(bungeeConnectEvent);
         if (bungeeConnectEvent.isCancelled()) {
            return;
         }

         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("Connect");
         out.writeUTF(server);
         player.sendPluginMessage(CorePlugin.getInstance(), "BungeeCord", out.toByteArray());
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}
