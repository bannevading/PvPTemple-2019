package server.pvptemple.listener;

import com.google.gson.JsonObject;
import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.util.ArrayList;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.LogRequest;
import server.pvptemple.board.Board;
import server.pvptemple.event.player.MinemanRetrieveEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.redis.CoreRedisManager;
import server.pvptemple.redis.MessageType;
import server.pvptemple.util.BanWrapper;
import server.pvptemple.util.MessageFilter;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;
import server.pvptemple.util.finalutil.TimeUtil;

public class PlayerListener implements Listener {
   private final CorePlugin plugin;

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onAsyncPlayerPreLoginLow(AsyncPlayerPreLoginEvent event) {
      if (!CorePlugin.SETUP) {
         event.disallow(Result.KICK_OTHER, CC.RED + "Server is setting up...");
      } else {
         this.plugin.getPlayerManager().addPlayer(event.getUniqueId(), event.getName(), event.getAddress());
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onAsyncPlayerPreLoginHigh(AsyncPlayerPreLoginEvent event) {
      if (event.getLoginResult() == Result.ALLOWED) {
         Mineman mineman = this.plugin.getPlayerManager().getPlayer(event.getUniqueId());
         boolean isLobby = CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().startsWith("lobby-");
         BanWrapper wrapper = mineman.fetchData();
         if (mineman.isBlacklisted() || wrapper.isBanned() && !isLobby) {
            event.disallow(Result.KICK_BANNED, wrapper.getMessage());
         }

      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerLogin(PlayerLoginEvent event) {
      Player player = event.getPlayer();
      if (Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size() && !PlayerUtil.testPermission(player, Rank.BASIC)) {
         event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.RED + "The server is currently full...");
      } else {
         event.allow();
      }

   }

   @EventHandler
   public void onMinemanRetrieve(MinemanRetrieveEvent event) {
      if (event.getBanWrapper().isBanned() && !CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().startsWith("lobby-")) {
         Player player = event.getMineman().getPlayer();
         if (player != null) {
            player.kickPlayer(event.getBanWrapper().getMessage());
         }
      } else {
         Mineman mineman = event.getMineman();
         Player player = this.plugin.getServer().getPlayer(mineman.getUuid());
         if (player != null) {
            mineman.onJoin();
            player.setPlayerListName(mineman.getRank().getColor() + player.getName() + CC.R);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onJoin(PlayerJoinEvent event) {
      event.setJoinMessage((String)null);
      Player player = event.getPlayer();
      Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
      if (mineman != null && mineman.isErrorLoadingData()) {
         player.kickPlayer(StringUtil.LOAD_ERROR);
      } else if (mineman != null && mineman.isDataLoaded()) {
         if (mineman.getBanData().isBanned() && !CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().startsWith("lobby-")) {
            if (this.plugin.getPlayerManager().getDummyPlayers().contains(player.getUniqueId())) {
               Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                  JsonObject object = new JsonObject();
                  object.addProperty("type", MessageType.FORCE_REMOVE.toString());
                  object.addProperty("uuid", player.getUniqueId().toString());
                  this.plugin.getServerManager().getProxyPublisher().write(object);
               });
               Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                  PlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
                  playerList.disconnect(((CraftPlayer)player).getHandle());
               }, 1L);
            } else {
               player.kickPlayer(mineman.getBanData().getMessage());
            }
         } else {
            mineman.onJoin();
            if (this.plugin.getBoardManager() != null) {
               this.plugin.getBoardManager().getPlayerBoards().put(player.getUniqueId(), new Board(player, this.plugin.getBoardManager().getAdapter()));
            }

            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
               Mineman mineman1 = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
               if (mineman1 != null) {
                  mineman1.updateTabList(mineman.getDisplayRank());
               }

            }, 5L);

            for(Mineman mineman2 : this.plugin.getPlayerManager().getPlayers().values()) {
               if (mineman2.isVanishMode() && mineman.getRank().getPriority() <= mineman2.getRank().getPriority()) {
                  player.hidePlayer(mineman2.getPlayer());
               }
            }

         }
      } else {
         player.kickPlayer(StringUtil.LOAD_ERROR);
      }
   }

   @EventHandler
   public void onPlayerKick(PlayerKickEvent event) {
      Player player = event.getPlayer();
      this.plugin.getPlayerManager().removePlayer(player.getUniqueId());
      if (this.plugin.getBoardManager() != null) {
         this.plugin.getBoardManager().getPlayerBoards().remove(player.getUniqueId());
      }

   }

   @EventHandler
   public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
      Player player = event.getPlayer();
      Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
      this.plugin.getRequestProcessor().sendRequestAsync(new LogRequest.CommandLogRequest(event.getMessage(), mineman.getId()));
      if (!mineman.hasRank(Rank.TRAINEE)) {
         if (!player.isOp()) {
            if (System.currentTimeMillis() < mineman.getCommandCooldown()) {
               event.setCancelled(true);
               player.sendMessage(StringUtil.COMMAND_COOLDOWN);
            } else {
               mineman.setCommandCooldown(System.currentTimeMillis() + 1000L);
            }

         }
      }
   }

   @EventHandler
   public void onItemPickup(PlayerPickupItemEvent event) {
      if (event.getItem().getItemStack().getType() == Material.BED) {
         event.setCancelled(true);
      }

      if (this.plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).isVanishMode()) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      if (this.plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).isVanishMode()) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockBreak(BlockPlaceEvent event) {
      if (this.plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).isVanishMode()) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
      if (this.plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).isVanishMode()) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent event) {
      if (this.plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()).isVanishMode()) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onEntityTarget(EntityTargetEvent event) {
      if (event.getReason() != TargetReason.CUSTOM) {
         Entity entity = event.getEntity();
         Entity target = event.getTarget();
         if ((entity instanceof ExperienceOrb || entity instanceof LivingEntity) && target instanceof Player && this.plugin.getPlayerManager().getPlayer(target.getUniqueId()).isVanishMode()) {
            event.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      DamageCause cause = event.getCause();
      if (cause != DamageCause.VOID && cause != DamageCause.SUICIDE) {
         Entity entity = event.getEntity();
         if (entity instanceof Player) {
            Player player = (Player)entity;
            if (this.plugin.getPlayerManager().getPlayer(player.getUniqueId()).isVanishMode()) {
               event.setCancelled(true);
            }
         }

      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      DamageCause cause = event.getCause();
      if (cause != DamageCause.VOID && cause != DamageCause.SUICIDE) {
         Entity entity = event.getEntity();
         if (entity instanceof Player) {
            Player player = (Player)entity;
            if (this.plugin.getPlayerManager().getPlayer(player.getUniqueId()).isVanishMode()) {
               event.setCancelled(true);
            }
         }

         if (event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();
            if (this.plugin.getPlayerManager().getPlayer(damager.getUniqueId()).isVanishMode()) {
               event.setCancelled(true);
            }
         }

      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      event.setQuitMessage((String)null);
      Player player = event.getPlayer();
      this.plugin.getPlayerManager().removePlayer(player.getUniqueId());
      if (this.plugin.getBoardManager() != null) {
         this.plugin.getBoardManager().getPlayerBoards().remove(player.getUniqueId());
      }

      this.plugin.getDisguiseManager().getDisguiseData().remove(player.getUniqueId());
      this.plugin.getDisguiseManager().getOriginalCache().remove(player.getUniqueId());
   }

   @EventHandler
   public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
      if (mineman.isMuted()) {
         if (mineman.getMuteTime() == null || System.currentTimeMillis() - mineman.getMuteTime().getTime() <= 0L) {
            if (mineman.getMuteTime() == null) {
               player.sendMessage(StringUtil.PERMANENT_MUTE);
            } else {
               player.sendMessage(String.format(StringUtil.TEMPORARY_MUTE, TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - mineman.getMuteTime().getTime()))));
            }

            event.setCancelled(true);
            return;
         }

         mineman.setMuted(false);
         mineman.setMuteTime(new Timestamp(0L));
      }

      if (mineman.isBanned()) {
         player.sendMessage(CC.RED + "You cannot talk while you're banned!");
         event.setCancelled(true);
      } else {
         Rank rank = mineman.getRank();
         if (CoreRedisManager.handleMessage(mineman, event.getMessage())) {
            event.setCancelled(true);
         } else if (this.plugin.getPlayerManager().isChatSilenced() && !rank.hasRank(Rank.HOST)) {
            player.sendMessage(CC.RED + "Public chat is currently muted.");
            event.setCancelled(true);
         } else {
            if (!mineman.hasRank(Rank.BASIC)) {
               long slowChat = this.plugin.getPlayerManager().getChatSlowDownTime();
               if (System.currentTimeMillis() < mineman.getChatCooldown()) {
                  player.sendMessage(slowChat > 0L ? StringUtil.SLOW_CHAT : StringUtil.CHAT_COOLDOWN);
                  event.setCancelled(true);
                  return;
               }

               mineman.setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
            }

            rank = mineman.getDisplayRank();
            if (MessageFilter.shouldFilter(event.getMessage())) {
               if (!mineman.hasRank(Rank.HOST)) {
                  String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : rank.getColor();
                  String formattedMessage = String.format(rank.getPrefix() + color + "%1$s" + CC.R + ": %2$s", player.getName(), event.getMessage());
                  PlayerUtil.messageRank(CC.RED + "[Filtered] " + formattedMessage);
                  player.sendMessage(formattedMessage);
                  event.setCancelled(true);
                  return;
               }

               player.sendMessage(CC.RED + "That would have been filtered.");
            }

            if (!event.getMessage().contains("IÌ‡") && !event.getMessage().contains("İ")) {
               if (!PlayerUtil.testPermission(player, Rank.HOST) && this.plugin.getFilterManager().isFiltered(player, mineman, event.getMessage())) {
                  event.setCancelled(true);
               } else {
                  String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : rank.getColor();
                  event.setFormat(rank.getPrefix() + color + player.getName() + CC.R + ": %2$s");

                  for(Player recipient : new ArrayList<>(event.getRecipients())) {
                     Mineman recipientMineman = this.plugin.getPlayerManager().getPlayer(recipient.getUniqueId());
                     if (!mineman.hasRank(Rank.TRAINEE) && recipientMineman != null && recipientMineman.isDataLoaded() && (recipientMineman.isIgnoring(mineman.getId()) || !recipientMineman.isChatEnabled())) {
                        event.getRecipients().remove(recipient);
                     }
                  }

               }
            } else {
               event.setCancelled(true);
               this.plugin.getFilterManager().handleCommand("mute " + player.getName() + " Sending crash codes -s");
               player.sendMessage(Color.translate("&cYou have been muted for &eCrash Codes&c."));
               player.sendMessage(Color.translate("&cIf you beleive this is false, join our TeamSpeak (ts.pvptemple.com)"));
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getClickedInventory() != null && event.getClickedInventory().getTitle().contains("Inventory: ")) {
         ItemStack stack = event.getCurrentItem();
         if (stack == null || stack.getType() == Material.AIR || !stack.hasItemMeta()) {
            return;
         }

         Player player = (Player)event.getWhoClicked();
         if (!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            event.setCancelled(true);
         }
      }

   }

   @ConstructorProperties({"plugin"})
   public PlayerListener(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
