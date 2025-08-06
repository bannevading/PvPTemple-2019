package server.pvptemple.entity.wrapper;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class DummyConnectionWrapper extends PlayerConnection {
   private boolean disconnected = false;

   public DummyConnectionWrapper(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
      super(minecraftserver, networkmanager, entityplayer);
   }

   public CraftPlayer getPlayer() {
      return this.player == null ? null : this.player.getBukkitEntity();
   }

   public void c() {
   }

   public NetworkManager b() {
      return this.networkManager;
   }

   public void disconnect(String s) {
      WorldServer worldserver = (WorldServer)this.player.getWorld();
      worldserver.kill(this.player);
      worldserver.getPlayerChunkMap().removePlayer(this.player);
      ((CraftServer)Bukkit.getServer()).getHandle().players.remove(this.player);
      ((CraftServer)Bukkit.getServer()).getHandle().disconnect(this.player);
      this.disconnected = true;
   }

   public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
   }

   public void a(PacketPlayInFlying packetplayinflying) {
   }

   public void a(double d0, double d1, double d2, float f, float f1) {
   }

   public void teleport(Location dest) {
   }

   public void sendPacket(Packet packet) {
   }

   public boolean isDisconnected() {
      return true;
   }
}
