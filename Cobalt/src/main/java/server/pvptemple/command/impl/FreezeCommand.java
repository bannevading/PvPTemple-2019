package server.pvptemple.command.impl;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;

public class FreezeCommand implements CommandHandler, Listener {
   private final Map<UUID, CustomLocation> frozenPlayers = new HashMap();

   public FreezeCommand() {
      CorePlugin.getInstance().getServer().getScheduler().runTaskTimer(CorePlugin.getInstance(), new FrozenMessageTask(this), 200L, 20L);
   }

   @Command(
      name = {"ss", "screenshare", "freeze"},
      rank = Rank.TRAINEE,
      description = "Freeze a player"
   )
   public void onFreeze(Player player, @Param(name = "target") Player target) {
      if (target != null && target.isOnline()) {
         if (this.frozenPlayers.remove(target.getUniqueId()) != null) {
            PlayerUtil.messageRank(CC.SECONDARY + target.getDisplayName() + CC.YELLOW + " was unfrozen by " + CC.SECONDARY + player.getDisplayName() + CC.YELLOW + ".", Rank.TRAINEE);
            target.sendMessage(CC.GREEN + "You are no longer frozen!");
            target.setAllowFlight(false);
            target.setFlying(false);
         } else {
            CustomLocation location = CustomLocation.fromBukkitLocation(target.getLocation());
            this.frozenPlayers.put(target.getUniqueId(), location);
            PlayerUtil.messageRank(CC.SECONDARY + target.getDisplayName() + CC.YELLOW + " was frozen by " + CC.SECONDARY + player.getDisplayName() + CC.YELLOW + ".", Rank.TRAINEE);
            target.setAllowFlight(true);
            target.setFlying(true);
         }

      } else {
         player.sendMessage(CC.RED + "Failed to find that player.");
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      if (this.frozenPlayers.remove(event.getPlayer().getUniqueId()) != null) {
         PlayerUtil.messageRank("");
         PlayerUtil.messageRank(CC.BD_RED + event.getPlayer().getName() + " has logged out while frozen.");
         PlayerUtil.messageRank("");
      }

   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent event) {
      if (this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
         this.frozenPlayers.put(event.getPlayer().getUniqueId(), CustomLocation.fromBukkitLocation(event.getTo()));
      }

   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (this.frozenPlayers.containsKey(event.getEntity().getUniqueId())) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onCommand(PlayerCommandPreprocessEvent event) {
      String command = event.getMessage().split(" ")[0];
      if (!command.equalsIgnoreCase("msg") && !command.equalsIgnoreCase("r") && !command.equalsIgnoreCase("m") && !command.equalsIgnoreCase("tell") && !command.equalsIgnoreCase("message")) {
         if (this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
      Rank rank = mineman.getRank();
      if (!this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
         if (!rank.hasRank(Rank.TRAINEE)) {
            event.getRecipients().removeIf((player) -> this.frozenPlayers.containsKey(player.getUniqueId()));
         }

      } else {
         event.setCancelled(true);
         String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : rank.getColor();
         String formattedMessage = String.format(rank.getPrefix() + color + "%1$s" + CC.R + ": %2$s", event.getPlayer().getName(), event.getMessage());
         PlayerUtil.messageRank(CC.D_RED + "[Frozen] " + formattedMessage);
         event.getPlayer().sendMessage(formattedMessage);
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageByEntityEvent event) {
      if (this.frozenPlayers.containsKey(event.getDamager().getUniqueId())) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onDropItem(PlayerDropItemEvent event) {
      if (this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
         event.setCancelled(true);
      }

   }

   private class FrozenMessageTask implements Runnable {
      private final FreezeCommand command;
      private int count = 0;

      public void run() {
         Set<UUID> remove = new HashSet();
         this.command.frozenPlayers.forEach((uuidx, location) -> {
            Player player = CorePlugin.getInstance().getServer().getPlayer(uuidx);
            if (player == null) {
               remove.add(uuidx);
            } else {
               ++this.count;
               if (this.count == 3) {
                  player.sendMessage("");
                  player.sendMessage(Color.translate("&4&l Do not disconnect!"));
                  player.sendMessage(Color.translate("&c If you disconnect, you will be banned."));
                  player.sendMessage("");
                  player.sendMessage(Color.translate("&7 Download &fTeamSpeak &7and connect to &cts.pvptemple.com&7. Do not change, edit, or delete any files on your computer."));
                  player.sendMessage("");
                  this.count = 0;
               }

               Location location1 = location.toBukkitLocation();
               location1.setPitch(player.getLocation().getPitch());
               location1.setYaw(player.getLocation().getYaw());
               player.teleport(location1);
            }
         });

         for(UUID uuid : remove) {
            this.command.frozenPlayers.remove(uuid);
         }

      }

      @ConstructorProperties({"command"})
      public FrozenMessageTask(FreezeCommand command) {
         this.command = command;
      }
   }
}
