package server.pvptemple.runnable;

import java.beans.ConstructorProperties;
import server.pvptemple.Carbon;
import server.pvptemple.arena.Arena;
import server.pvptemple.arena.StandaloneArena;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.finalutil.CC;

public class ArenaCommandRunnable implements Runnable {
   private final Carbon plugin;
   private final Arena copiedArena;
   private int times;

   public void run() {
      this.duplicateArena(this.copiedArena, 1000, 0);
   }

   private void duplicateArena(final Arena arena, int offsetX, int offsetZ) {
      (new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {
         public void onComplete() {
            double minX = arena.getMin().getX() + (double)this.getOffsetX();
            double minZ = arena.getMin().getZ() + (double)this.getOffsetZ();
            double maxX = arena.getMax().getX() + (double)this.getOffsetX();
            double maxZ = arena.getMax().getZ() + (double)this.getOffsetZ();
            double aX = arena.getA().getX() + (double)this.getOffsetX();
            double aZ = arena.getA().getZ() + (double)this.getOffsetZ();
            double bX = arena.getB().getX() + (double)this.getOffsetX();
            double bZ = arena.getB().getZ() + (double)this.getOffsetZ();
            CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ);
            CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ);
            CustomLocation a = new CustomLocation(aX, arena.getA().getY(), aZ, arena.getA().getYaw(), arena.getA().getPitch());
            CustomLocation b = new CustomLocation(bX, arena.getB().getY(), bZ, arena.getB().getYaw(), arena.getB().getPitch());
            StandaloneArena standaloneArena = new StandaloneArena(a, b, min, max, (CustomLocation)null, (CustomLocation)null);
            if (arena.getBedA() != null) {
               standaloneArena.setBedA(new CustomLocation(arena.getBedA().getX() + (double)this.getOffsetX(), arena.getBedA().getY(), arena.getBedA().getZ() + (double)this.getOffsetZ()));
            }

            if (arena.getBedB() != null) {
               standaloneArena.setBedB(new CustomLocation(arena.getBedB().getX() + (double)this.getOffsetX(), arena.getBedB().getY(), arena.getBedB().getZ() + (double)this.getOffsetZ()));
            }

            arena.addStandaloneArena(standaloneArena);
            arena.addAvailableArena(standaloneArena);
            if (--ArenaCommandRunnable.this.times > 0) {
               ArenaCommandRunnable.this.plugin.getServer().broadcastMessage(CC.PRIMARY + "Placed a standalone arena of " + CC.SECONDARY + arena.getName() + CC.PRIMARY + " at " + CC.SECONDARY + minX + CC.PRIMARY + ", " + CC.SECONDARY + minZ + CC.PRIMARY + ". " + CC.SECONDARY + ArenaCommandRunnable.this.times + CC.PRIMARY + " arenas remaining.");
               ArenaCommandRunnable.this.duplicateArena(arena, (int)Math.round(maxX), (int)Math.round(maxZ));
            } else {
               ArenaCommandRunnable.this.plugin.getServer().broadcastMessage(CC.PRIMARY + "Finished pasting " + CC.SECONDARY + ArenaCommandRunnable.this.copiedArena.getName() + CC.PRIMARY + "'s standalone arenas.");
               ArenaCommandRunnable.this.plugin.getArenaManager().setGeneratingArenaRunnables(ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnables() - 1);
            }

         }
      }).run();
   }

   public Carbon getPlugin() {
      return this.plugin;
   }

   public Arena getCopiedArena() {
      return this.copiedArena;
   }

   public int getTimes() {
      return this.times;
   }

   @ConstructorProperties({"plugin", "copiedArena", "times"})
   public ArenaCommandRunnable(Carbon plugin, Arena copiedArena, int times) {
      this.plugin = plugin;
      this.copiedArena = copiedArena;
      this.times = times;
   }
}
