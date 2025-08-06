package server.pvptemple.entity.wrapper;

import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class PlayerInteractWrapper extends PlayerInteractManager {
   public PlayerInteractWrapper(World world) {
      super(((CraftWorld)world).getHandle());
   }

   public PlayerInteractWrapper(WorldServer worldServer) {
      super(worldServer);
   }
}
