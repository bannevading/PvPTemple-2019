package server.pvptemple.handler;

import com.minexd.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.oitc.OITCPlayer;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.events.sumo.SumoPlayer;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchState;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;

public class CustomMovementHandler implements MovementHandler {
   private final Carbon plugin = Carbon.getInstance();

   public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData == null) {
         this.plugin.getLogger().warning(player.getName() + "'s player data is null");
      } else {
         if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isSpleef() || match.getKit().isSumo()) {
               Location location = player.getLocation();
               location.setY(location.getY() - (double)1.0F);
               if (location.getBlock().getType().equals(Material.WATER) || location.getBlock().getType().equals(Material.STATIONARY_WATER)) {
                  this.plugin.getMatchManager().removeFighter(player, playerData, true);
               }

               if ((to.getX() != from.getX() || to.getZ() != from.getZ()) && match.getMatchState() == MatchState.STARTING) {
                  player.teleport(from);
                  ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
               }
            }
         }

         PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
         if (event != null) {
            if (event instanceof SumoEvent) {
               SumoEvent sumoEvent = (SumoEvent)event;
               if (((SumoPlayer)sumoEvent.getPlayer(player)).getFighting() != null && ((SumoPlayer)sumoEvent.getPlayer(player)).getState() == SumoPlayer.SumoState.PREPARING) {
                  player.teleport(from);
                  ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
               }
            } else if (event instanceof OITCEvent) {
               OITCEvent oitcEvent = (OITCEvent)event;
               if (((OITCPlayer)oitcEvent.getPlayer(player)).getState() == OITCPlayer.OITCState.RESPAWNING) {
                  ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
               }
            }
         }

      }
   }

   public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
   }
}
