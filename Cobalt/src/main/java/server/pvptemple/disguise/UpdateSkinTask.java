package server.pvptemple.disguise;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.beans.ConstructorProperties;
import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.util.other.GameProfileUtil;

public class UpdateSkinTask extends BukkitRunnable {
   private final CorePlugin plugin;
   private final Player player;
   private final GameProfile newProfileData;
   private final String displayName;

   public void run() {
      EntityPlayer entityPlayer = ((CraftPlayer)this.player).getHandle();
      this.setPlayerNames();

      try {
         Field field = EntityHuman.class.getDeclaredField("bH");
         field.setAccessible(true);
         GameProfile currentProfile = (GameProfile)field.get(entityPlayer);
         currentProfile.getProperties().clear();

         for(Property property : this.newProfileData.getProperties().values()) {
            currentProfile.getProperties().put(property.getName(), property);
         }

         GameProfileUtil.setName(currentProfile, this.displayName);
         field.set(entityPlayer, currentProfile);
      } catch (Exception e) {
         e.printStackTrace();
         return;
      }

      this.sendPlayerUpdate();
   }

   private void setPlayerNames() {
   }

   private void sendPlayerUpdate() {
      (new BukkitRunnable() {
         public void run() {
            UpdateSkinTask.this.sendUpdateToPlayer();
            UpdateSkinTask.this.plugin.getServer().getOnlinePlayers().stream().filter((other) -> !other.equals(UpdateSkinTask.this.player)).filter((other) -> other.canSee(UpdateSkinTask.this.player)).forEach((other) -> {
               other.hidePlayer(UpdateSkinTask.this.player);
               other.showPlayer(UpdateSkinTask.this.player);
            });
            Mineman mineman = UpdateSkinTask.this.plugin.getPlayerManager().getPlayer(UpdateSkinTask.this.player.getUniqueId());
            if (mineman != null) {
               mineman.updateTabList(mineman.getDisplayRank());
            }
         }
      }).runTask(this.plugin);
   }

   private void sendUpdateToPlayer() {
      Entity vehicle = this.player.getVehicle();
      if (vehicle != null) {
         vehicle.eject();
      }

      this.sendPackets();
      this.player.updateInventory();
      this.player.setGameMode(this.player.getGameMode());
      PlayerInventory inventory = this.player.getInventory();
      inventory.setHeldItemSlot(inventory.getHeldItemSlot());
      double oldHealth = this.player.getHealth();
      int oldFood = this.player.getFoodLevel();
      float oldSat = this.player.getSaturation();
      this.player.setFoodLevel(20);
      this.player.setFoodLevel(oldFood);
      this.player.setSaturation(5.0F);
      this.player.setSaturation(oldSat);
      this.player.setMaxHealth(this.player.getMaxHealth());
      this.player.setHealth((double)20.0F);
      this.player.setHealth(oldHealth);
      float experience = this.player.getExp();
      int totalExperience = this.player.getTotalExperience();
      this.player.setExp(experience);
      this.player.setTotalExperience(totalExperience);
      this.player.setWalkSpeed(this.player.getWalkSpeed());
      this.player.setDisplayName(this.displayName);
   }

   private void sendPackets() {
      EntityPlayer entityPlayer = ((CraftPlayer)this.player).getHandle();
      Location previousLocation = this.player.getLocation().clone();
      entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{entityPlayer}));
      entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{entityPlayer}));
      entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(entityPlayer.getWorld().worldProvider.getDimension(), entityPlayer.getWorld().worldData.getDifficulty(), entityPlayer.getWorld().worldData.getType(), EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())));
      this.player.teleport(previousLocation);
   }

   @ConstructorProperties({"plugin", "player", "newProfileData", "displayName"})
   public UpdateSkinTask(CorePlugin plugin, Player player, GameProfile newProfileData, String displayName) {
      this.plugin = plugin;
      this.player = player;
      this.newProfileData = newProfileData;
      this.displayName = displayName;
   }
}
