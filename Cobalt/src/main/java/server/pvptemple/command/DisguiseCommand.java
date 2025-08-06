package server.pvptemple.command;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.redis.CoreRedisManager;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Flag;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class DisguiseCommand implements CommandHandler {
   private static final Rank MAX_RANK;
   private static final String MEME = " &^!@*%$*^%!@^$% ";
   private final CorePlugin plugin;

   @Command(
      name = {"disguise"},
      rank = Rank.PLATFORMADMIN
   )
   public void disguiseCommand(final Mineman mineman, @Flag(name = "b") boolean forceBypass, @Param(name = "name",defaultTo = " &^!@*%$*^%!@^$% ") final String name, @Param(name = "skin",defaultTo = " &^!@*%$*^%!@^$% ") String skin, @Param(name = "rank",defaultTo = "Normal") final Rank rank) {
      final Player player = mineman.getPlayer();
      if (!mineman.hasRank(Rank.DEVELOPER) && mineman.getRank() != Rank.PARTNER) {
         if (!this.plugin.getDisguiseManager().getAllowedDisguising().test(player)) {
            if (forceBypass) {
               player.sendMessage(CC.RED + "You're not allowed to force bypass the disguise check...");
            }

            return;
         }

         if (forceBypass) {
            forceBypass = false;
         }
      } else if (!forceBypass && !this.plugin.getDisguiseManager().getAllowedDisguising().test(player)) {
         return;
      }

      if (!forceBypass && rank.getPriority() > MAX_RANK.getPriority()) {
         player.sendMessage(CC.RED + "You're not allowed to disguise past the rank " + MAX_RANK.getPrefix() + MAX_RANK.getName() + CC.RED + ".");
      } else if (name.equalsIgnoreCase(" &^!@*%$*^%!@^$% ") || (name.equalsIgnoreCase("self") || name.equalsIgnoreCase(mineman.getName())) && skin.equalsIgnoreCase(" &^!@*%$*^%!@^$% ")) {
         if (this.plugin.getDisguiseManager().isDisguised(player)) {
            boolean finalForceBypass = forceBypass;
            (new BukkitRunnable() {
               public void run() {
                  DisguiseCommand.this.plugin.getCoreRedisManager().sendMessage(CoreRedisManager.getServerMessagePrefix() + mineman.getRank().getColor() + mineman.getName() + CC.AQUA + " has undisguised from " + mineman.getDisguiseRank().getColor() + player.getDisplayName() + CC.AQUA + ".", finalForceBypass ? Rank.DEVELOPER : Rank.MOD);
                  mineman.setDisguiseRank((Rank)null);
                  DisguiseCommand.this.plugin.getDisguiseManager().undisguise(player);
                  player.sendMessage(CC.RED + "You've undisguised.");
               }
            }).runTaskAsynchronously(this.plugin);
         } else {
            throw new IllegalArgumentException("lol");
         }
      } else {
         if (skin.equalsIgnoreCase(" &^!@*%$*^%!@^$% ")) {
            skin = player.getName();
         }

         if (skin.length() > 16) {
            player.sendMessage(CC.RED + "The skin you're trying to pick is too long.");
         } else if (name.length() > 16) {
            player.sendMessage(CC.RED + "The name you're trying to pick is too long.");
         } else {
            boolean finalForceBypass1 = forceBypass;
            String finalSkin = skin;
            (new BukkitRunnable() {
               public void run() {
                  try {
                     if (DisguiseCommand.this.plugin.getDisguiseManager().disguise(player, finalSkin, name)) {
                        DisguiseCommand.this.plugin.getCoreRedisManager().sendMessage(CoreRedisManager.getServerMessagePrefix() + mineman.getRank().getColor() + mineman.getName() + CC.AQUA + " has disguised as " + rank.getColor() + name + CC.AQUA + " with the skin " + CC.BLUE + finalSkin + CC.AQUA + ".", finalForceBypass1 ? Rank.DEVELOPER : Rank.MOD);
                        mineman.setDisguiseRank(rank);
                        if (name.equalsIgnoreCase(player.getName()) && rank.equals(mineman.getDisplayRank())) {
                           player.sendMessage(CC.SECONDARY + "You've updated your skin to " + CC.PRIMARY + finalSkin + CC.SECONDARY + ".");
                        } else {
                           player.sendMessage(CC.SECONDARY + "You've set your display name to " + CC.PRIMARY + name + CC.SECONDARY + ", your skin is now displaying as " + CC.PRIMARY + finalSkin + CC.SECONDARY + " with the rank " + rank.getPrefix() + rank.getName() + CC.SECONDARY + ".");
                        }
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                     player.sendMessage(CC.RED + "There was an issue while disgusing!");
                  }

               }
            }).runTaskAsynchronously(this.plugin);
         }
      }
   }

   @ConstructorProperties({"plugin"})
   public DisguiseCommand(CorePlugin plugin) {
      this.plugin = plugin;
   }

   static {
      MAX_RANK = Rank.MASTER;
   }
}
