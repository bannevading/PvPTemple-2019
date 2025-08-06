package server.pvptemple.commands.management;

import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.arena.Arena;
import server.pvptemple.rank.Rank;
import server.pvptemple.runnable.ArenaCommandRunnable;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class ArenaCommand extends Command {
   private static final String NO_ARENA;
   private final Carbon plugin = Carbon.getInstance();

   public ArenaCommand() {
      super("arena");
      this.setDescription("Manage server arenas.");
      this.setUsage(CC.RED + "Usage: /arena <subcommand> [args]");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (sender instanceof Player && PlayerUtil.testPermission(sender, Rank.ADMIN)) {
         if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return true;
         } else {
            Player player = (Player)sender;
            Arena arena = this.plugin.getArenaManager().getArena(args[1]);
            switch (args[0].toLowerCase()) {
               case "create":
                  if (arena == null) {
                     this.plugin.getArenaManager().createArena(args[1]);
                     sender.sendMessage(CC.GREEN + "Successfully created arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(CC.RED + "That arena already exists!");
                  }
                  break;
               case "delete":
                  if (arena != null) {
                     this.plugin.getArenaManager().deleteArena(args[1]);
                     sender.sendMessage(CC.GREEN + "Successfully deleted arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(NO_ARENA);
                  }
                  break;
               case "a":
                  if (arena != null) {
                     Location location = player.getLocation();
                     if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX((double)location.getBlockX() + (double)0.5F);
                        location.setY((double)location.getBlockY() + (double)3.0F);
                        location.setZ((double)location.getBlockZ() + (double)0.5F);
                     }

                     arena.setA(CustomLocation.fromBukkitLocation(location));
                     sender.sendMessage(CC.GREEN + "Successfully set position A for arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(NO_ARENA);
                  }
                  break;
               case "b":
                  if (arena != null) {
                     Location location = player.getLocation();
                     if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX((double)location.getBlockX() + (double)0.5F);
                        location.setY((double)location.getBlockY() + (double)3.0F);
                        location.setZ((double)location.getBlockZ() + (double)0.5F);
                     }

                     arena.setB(CustomLocation.fromBukkitLocation(location));
                     sender.sendMessage(CC.GREEN + "Successfully set position B for arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(NO_ARENA);
                  }
                  break;
               case "min":
                  if (arena != null) {
                     arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                     sender.sendMessage(CC.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(NO_ARENA);
                  }
                  break;
               case "max":
                  if (arena != null) {
                     arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                     sender.sendMessage(CC.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(NO_ARENA);
                  }
                  break;
               case "beda":
                  if (arena != null) {
                     Block block = player.getTargetBlock((HashSet<Byte>) null, 200);
                     if (!block.getType().equals(Material.BED_BLOCK)) {
                        player.sendMessage(ChatColor.RED + "You must look at a bed.");
                        return true;
                     }

                     arena.setBedA(CustomLocation.fromBukkitLocation(block.getLocation()));
                  }
                  break;
               case "bedb":
                  Block block = player.getTargetBlock((HashSet<Byte>) null, 200);
                  if (!block.getType().equals(Material.BED_BLOCK)) {
                     player.sendMessage(ChatColor.RED + "You must look at a bed.");
                     return true;
                  }

                  arena.setBedB(CustomLocation.fromBukkitLocation(block.getLocation()));
                  break;
               case "disable":
               case "enable":
                  if (arena != null) {
                     arena.setEnabled(!arena.isEnabled());
                     sender.sendMessage(arena.isEnabled() ? CC.GREEN + "Successfully enabled arena " + args[1] + "." : CC.RED + "Successfully disabled arena " + args[1] + ".");
                  } else {
                     sender.sendMessage(NO_ARENA);
                  }
                  break;
               case "generate":
                  if (args.length == 3) {
                     int arenas = Integer.parseInt(args[2]);
                     this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
                     this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
                  } else {
                     sender.sendMessage(CC.RED + "Usage: /arena generate <arena> <arenas>");
                  }
                  break;
               default:
                  sender.sendMessage(this.usageMessage);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   static {
      NO_ARENA = CC.RED + "That arena doesn't exist!";
   }
}
