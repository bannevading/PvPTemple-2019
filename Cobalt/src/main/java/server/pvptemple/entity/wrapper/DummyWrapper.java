package server.pvptemple.entity.wrapper;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerList;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import server.pvptemple.CorePlugin;
import server.pvptemple.entity.EntityInteraction;
import server.pvptemple.entity.wrapper.DummyConnectionWrapper;
import server.pvptemple.entity.wrapper.PlayerInteractWrapper;

public class DummyWrapper extends EntityPlayer {
   private static Field GAME_PROFILE_FIELD;
   private final World world;
   @Setter
   @Getter
   private EntityInteraction entityInteraction;

   public DummyWrapper(World world, UUID uuid) {
      super(((CraftWorld)world).getHandle().getMinecraftServer(), ((CraftWorld)world).getHandle(), new GameProfile(uuid, Bukkit.getOfflinePlayer(uuid).getName()), new PlayerInteractWrapper(world));
      this.world = world;
      this.collidesWithEntities = false;
      this.playerConnection = new DummyConnectionWrapper(((CraftWorld)world).getHandle().getMinecraftServer(), new NetworkManager(EnumProtocolDirection.SERVERBOUND), this);
      if (GAME_PROFILE_FIELD == null) {
         try {
            GAME_PROFILE_FIELD = PacketPlayOutPlayerInfo.PlayerInfoData.class.getDeclaredField("d");
         } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
         }
      }

   }

   public void spawn() {
      PlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
      String ip = ThreadLocalRandom.current().nextInt(11, 256) + "." + ThreadLocalRandom.current().nextInt(256) + "." + ThreadLocalRandom.current().nextInt(256) + "." + ThreadLocalRandom.current().nextInt(256);

      AsyncPlayerPreLoginEvent asyncEvent;
      try {
         asyncEvent = new AsyncPlayerPreLoginEvent(this.getProfile().getName(), InetAddress.getByName(ip), this.getProfile().getId());
      } catch (Exception var5) {
         return;
      }

      Bukkit.getPluginManager().callEvent(asyncEvent);
      Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
         PlayerSpawnLocationEvent ev = new PlayerSpawnLocationEvent(this.getBukkitEntity(), this.getBukkitEntity().getLocation());
         Bukkit.getPluginManager().callEvent(ev);
         Location loc = ev.getSpawnLocation();
         WorldServer world = ((CraftWorld)loc.getWorld()).getHandle();
         this.spawnIn(world);
         this.setPosition(loc.getX(), loc.getY(), loc.getZ());
         this.setYawPitch(loc.getYaw(), loc.getPitch());
         playerList.onPlayerJoin(this, (String)null);
      });
   }

   public void g(float f, float f1) {
   }

   public boolean isSpectator() {
      return false;
   }

}
