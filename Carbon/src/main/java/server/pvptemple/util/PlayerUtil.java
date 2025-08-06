package server.pvptemple.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import com.minexd.spigot.SpigotX;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.board.Board;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.finalutil.CC;

public final class PlayerUtil {
   private PlayerUtil() {
   }

   public static void updateNametag(Player player, boolean hearts) {
      Bukkit.getScheduler().runTaskAsynchronously(Carbon.getInstance(), () -> {
         Board board = (Board)CorePlugin.getInstance().getBoardManager().getPlayerBoards().get(player.getUniqueId());
         if (board != null) {
            Scoreboard scoreboard = board.getScoreboard();
            Team red = scoreboard.getTeam("red");
            if (red == null) {
               red = scoreboard.registerNewTeam("red");
            }

            Team green = scoreboard.getTeam("green");
            if (green == null) {
               green = scoreboard.registerNewTeam("green");
            }

            red.setPrefix(CC.RED);
            green.setPrefix(CC.GREEN);
            PlayerData playerData = Carbon.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() != PlayerState.FIGHTING) {
               for(String entry : red.getEntries()) {
                  red.removeEntry(entry);
               }

               for(String entry : green.getEntries()) {
                  green.removeEntry(entry);
               }

            } else {
               Match match = Carbon.getInstance().getMatchManager().getMatch(player.getUniqueId());

               for(MatchTeam team : match.getTeams()) {
                  for(UUID teamUUID : team.getAlivePlayers()) {
                     Player teamPlayer = Bukkit.getPlayer(teamUUID);
                     if (teamPlayer != null) {
                        String teamPlayerName = teamPlayer.getName();
                        if (team.getTeamID() == playerData.getTeamID() && !match.isFFA()) {
                           if (!green.hasEntry(teamPlayerName)) {
                              green.addEntry(teamPlayerName);
                           }
                        } else if (!red.hasEntry(teamPlayerName)) {
                           red.addEntry(teamPlayerName);
                        }
                     }
                  }
               }

            }
         }
      });
   }

   public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
      for(int i = 0; i < player.getInventory().getContents().length; ++i) {
         ItemStack itemStack1 = player.getInventory().getContents()[i];
         if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
            player.getInventory().setItem(i, itemStack);
            break;
         }
      }

   }

   public static void respawnPlayer(final PlayerDeathEvent event) {
      (new BukkitRunnable() {
         public void run() {
            try {
               Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle").invoke(event.getEntity());
               Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
               Class<?> EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
               Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
               minecraftServer.setAccessible(true);
               Object mcserver = minecraftServer.get(con);
               Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
               Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, Integer.TYPE, Boolean.TYPE);
               moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
            } catch (Exception ex) {
               ex.printStackTrace();
            }

         }
      }).runTaskLater(Carbon.getInstance(), 2L);
   }

   public static void clearPlayer(Player player) {
      player.setHealth((double)20.0F);
      player.setFoodLevel(20);
      player.setSaturation(12.8F);
      player.setMaximumNoDamageTicks(20);
      player.setFireTicks(0);
      player.setFallDistance(0.0F);
      player.setLevel(0);
      player.setExp(0.0F);
      player.setWalkSpeed(0.2F);
      player.getInventory().setHeldItemSlot(0);
      player.setAllowFlight(false);
      player.getInventory().clear();
      player.getInventory().setArmorContents((ItemStack[])null);
      player.closeInventory();
      player.setGameMode(GameMode.SURVIVAL);
      player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
      ((CraftPlayer)player).getHandle().getDataWatcher().watch(9, (byte)0);
      EntityHuman entityhuman = ((CraftPlayer)player).getHandle();
      entityhuman.setKnockbackProfile(SpigotX.INSTANCE.getConfig().getKbProfileByName("default"));
      player.updateInventory();
   }

   public static void sendMessage(String message, Player... players) {
      for(Player player : players) {
         player.sendMessage(message);
      }

   }
}
