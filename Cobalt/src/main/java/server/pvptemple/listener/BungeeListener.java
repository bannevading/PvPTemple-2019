package server.pvptemple.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.beans.ConstructorProperties;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.pvptemple.CorePlugin;
import server.pvptemple.event.bungee.BungeeReceivedEvent;
import server.pvptemple.event.player.ModListRetrieveEvent;

public class BungeeListener implements PluginMessageListener {
   private final CorePlugin plugin;

   public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (channel.equals("BungeeCord")) {
         ByteArrayDataInput in = ByteStreams.newDataInput(message);
         String subChannel = in.readUTF();
         if (subChannel.equals("ForgeMods")) {
            try {
               Map<String, String> mods = (Map)(new JSONParser()).parse(in.readUTF());
               ModListRetrieveEvent event = new ModListRetrieveEvent(player, mods);
               this.plugin.getServer().getPluginManager().callEvent(event);
            } catch (ParseException e) {
               e.printStackTrace();
            }

         } else {
            short len = in.readShort();
            byte[] messageBytes = new byte[len];
            in.readFully(messageBytes);
            ByteArrayDataInput dis = ByteStreams.newDataInput(messageBytes);
            String data = dis.readUTF();
            long systemTime = Long.parseLong(data.split(":")[0]);
            BungeeReceivedEvent event = new BungeeReceivedEvent(player, subChannel, data.replace(systemTime + ":", ""), message, systemTime > System.currentTimeMillis());
            this.plugin.getServer().getPluginManager().callEvent(event);
         }
      }
   }

   @ConstructorProperties({"plugin"})
   public BungeeListener(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
