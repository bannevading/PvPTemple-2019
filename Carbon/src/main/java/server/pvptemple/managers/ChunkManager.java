package server.pvptemple.managers;

import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.arena.Arena;
import server.pvptemple.arena.StandaloneArena;
import server.pvptemple.util.CustomLocation;

public class ChunkManager {
   private final Carbon plugin = Carbon.getInstance();
   private boolean chunksLoaded;

   public ChunkManager() {
      CorePlugin.getInstance().setSetupSupplier(() -> this.chunksLoaded);
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, this::loadChunks, 1L);
   }

   private void loadChunks() {
      this.plugin.getLogger().info("Started loading all the chunks...");
      CustomLocation spawnMin = this.plugin.getSpawnManager().getSpawnMin();
      CustomLocation spawnMax = this.plugin.getSpawnManager().getSpawnMax();
      if (spawnMin != null && spawnMax != null) {
         int spawnMinX = spawnMin.toBukkitLocation().getBlockX() >> 4;
         int spawnMinZ = spawnMin.toBukkitLocation().getBlockZ() >> 4;
         int spawnMaxX = spawnMax.toBukkitLocation().getBlockX() >> 4;
         int spawnMaxZ = spawnMax.toBukkitLocation().getBlockZ() >> 4;
         if (spawnMinX > spawnMaxX) {
            int lastSpawnMinX = spawnMinX;
            spawnMinX = spawnMaxX;
            spawnMaxX = lastSpawnMinX;
         }

         if (spawnMinZ > spawnMaxZ) {
            int lastSpawnMinZ = spawnMinZ;
            spawnMinZ = spawnMaxZ;
            spawnMaxZ = lastSpawnMinZ;
         }

         for(int x = spawnMinX; x <= spawnMaxX; ++x) {
            for(int z = spawnMinZ; z <= spawnMaxZ; ++z) {
               spawnMin.toBukkitWorld().getChunkAt(x, z);
            }
         }
      }

      CustomLocation editorMin = this.plugin.getSpawnManager().getEditorMin();
      CustomLocation editorMax = this.plugin.getSpawnManager().getEditorMax();
      if (editorMin != null && editorMax != null) {
         int editorMinX = editorMin.toBukkitLocation().getBlockX() >> 4;
         int editorMinZ = editorMin.toBukkitLocation().getBlockZ() >> 4;
         int editorMaxX = editorMax.toBukkitLocation().getBlockX() >> 4;
         int editorMaxZ = editorMax.toBukkitLocation().getBlockZ() >> 4;
         if (editorMinX > editorMaxX) {
            int lastEditorMinX = editorMinX;
            editorMinX = editorMaxX;
            editorMaxX = lastEditorMinX;
         }

         if (editorMinZ > editorMaxZ) {
            int lastEditorMinZ = editorMinZ;
            editorMinZ = editorMaxZ;
            editorMaxZ = lastEditorMinZ;
         }

         for(int x = editorMinX; x <= editorMaxX; ++x) {
            for(int z = editorMinZ; z <= editorMaxZ; ++z) {
               editorMin.toBukkitWorld().getChunkAt(x, z);
            }
         }
      }

      for(Arena arena : this.plugin.getArenaManager().getArenas().values()) {
         if (arena.isEnabled()) {
            int arenaMinX = arena.getMin().toBukkitLocation().getBlockX() >> 4;
            int arenaMinZ = arena.getMin().toBukkitLocation().getBlockZ() >> 4;
            int arenaMaxX = arena.getMax().toBukkitLocation().getBlockX() >> 4;
            int arenaMaxZ = arena.getMax().toBukkitLocation().getBlockZ() >> 4;
            if (arenaMinX > arenaMaxX) {
               int lastArenaMinX = arenaMinX;
               arenaMinX = arenaMaxX;
               arenaMaxX = lastArenaMinX;
            }

            if (arenaMinZ > arenaMaxZ) {
               int lastArenaMinZ = arenaMinZ;
               arenaMinZ = arenaMaxZ;
               arenaMaxZ = lastArenaMinZ;
            }

            for(int x = arenaMinX; x <= arenaMaxX; ++x) {
               for(int z = arenaMinZ; z <= arenaMaxZ; ++z) {
                  arena.getMin().toBukkitWorld().getChunkAt(x, z);
               }
            }

            for(StandaloneArena saArena : arena.getStandaloneArenas()) {
               arenaMinX = saArena.getMin().toBukkitLocation().getBlockX() >> 4;
               arenaMinZ = saArena.getMin().toBukkitLocation().getBlockZ() >> 4;
               arenaMaxX = saArena.getMax().toBukkitLocation().getBlockX() >> 4;
               arenaMaxZ = saArena.getMax().toBukkitLocation().getBlockZ() >> 4;
               if (arenaMinX > arenaMaxX) {
                  int lastArenaMinX = arenaMinX;
                  arenaMinX = arenaMaxX;
                  arenaMaxX = lastArenaMinX;
               }

               if (arenaMinZ > arenaMaxZ) {
                  int lastArenaMinZ = arenaMinZ;
                  arenaMinZ = arenaMaxZ;
                  arenaMaxZ = lastArenaMinZ;
               }

               for(int x = arenaMinX; x <= arenaMaxX; ++x) {
                  for(int z = arenaMinZ; z <= arenaMaxZ; ++z) {
                     saArena.getMin().toBukkitWorld().getChunkAt(x, z);
                  }
               }
            }
         }
      }

      this.plugin.getLogger().info("Finished loading all the chunks!");
      this.chunksLoaded = true;
   }

   public boolean isChunksLoaded() {
      return this.chunksLoaded;
   }
}
