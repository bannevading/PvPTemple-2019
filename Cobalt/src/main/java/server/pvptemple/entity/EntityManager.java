package server.pvptemple.entity;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import server.pvptemple.CorePlugin;
import server.pvptemple.entity.wrapper.PlayerWrapper;
import server.pvptemple.util.CustomLocation;

public class EntityManager {
   @Getter
   private Map<Integer, PlayerWrapper> fakePlayers = new HashMap<>();

   public static Entity spawnEntity(EntityType entityType, CustomLocation location, String name) {
      Location bukkitLocation = location.toBukkitLocation();
      Entity entity = bukkitLocation.getWorld().spawnEntity(bukkitLocation, entityType);
      entity.setMetadata("custom", new FixedMetadataValue(CorePlugin.getInstance(), true));
      if (name != null) {
         entity.setCustomNameVisible(true);
         entity.setCustomName(name);
      }

      return entity;
   }

   public PlayerWrapper spawnPlayer(CustomLocation location, String name) {
      if (name.length() > 16) {
         throw new IllegalArgumentException(String.format("Name (%s) is longer than the maximum 16 characters.", name));
      } else {
         World world = location.toBukkitWorld();
         PlayerWrapper wrapper = new PlayerWrapper(world, name);
         wrapper.setLocation(location);
         this.fakePlayers.put(wrapper.getId(), wrapper);
         wrapper.spawn();
         return wrapper;
      }
   }
}
