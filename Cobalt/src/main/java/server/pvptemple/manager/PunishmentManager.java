package server.pvptemple.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.text.MessageFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.abstr.AbstractBukkitCallback;
import server.pvptemple.api.impl.PunishmentRequest;
import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.util.BanWrapper;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;
import server.pvptemple.util.finalutil.TimeUtil;

public class PunishmentManager {
   private final CorePlugin plugin;

   private void broadcastPunishment(String type, String reason, String punished, String punisher, boolean global) {
      Player player = this.plugin.getServer().getPlayer(punished);
      if (player != null) {
         punished = player.getName();
      }

      if (global) {
         this.plugin.getServer().broadcastMessage(CC.RED + punished + " was " + type + " by " + punisher + ".");
      } else {
         PlayerUtil.messageRank(CC.GRAY + "[Silent] " + CC.RED + punished + " was " + type + " by " + punisher + " for " + reason + ".");
      }

   }

   public void punish(CommandSender punisher, PunishCommand.PunishType type, String target, String reason, String ip, Timestamp expiry, boolean silent, boolean temporary) {
      int id = punisher instanceof Player ? this.plugin.getPlayerManager().getPlayer(((Player)punisher).getUniqueId()).getId() : -1;
      String finalType;
      if (type.getName().toLowerCase().startsWith("un")) {
         finalType = type.getName();
      } else if (temporary) {
         finalType = "temp-" + type.getName();
      } else {
         finalType = "perm-" + type.getName();
      }

      Player targetPlayer = this.plugin.getServer().getPlayer(target);
      if (type == PunishCommand.PunishType.KICK && targetPlayer == null) {
         punisher.sendMessage(CC.RED + "Player not online.");
      } else {
         String finalExpiryTime = expiry == null ? "" : TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - expiry.getTime()));
         this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final String server = CorePlugin.getInstance().getServerManager().getServerName();
            this.plugin.getRequestProcessor().sendRequest(new PunishmentRequest(expiry, ip, reason, CorePlugin.getRequestNameOrUUID(target), finalType, -1, id), new AbstractBukkitCallback() {
               public void callback(JsonElement element) {
                  JsonObject data = element.getAsJsonObject();
                  switch (data.get("response").getAsString()) {
                     case "player-never-joined":
                        punisher.sendMessage(CC.RED + "Player has never joined.");
                        break;
                     case "player-not-found":
                     case "invalid-player":
                        punisher.sendMessage(CC.RED + "Failed to find that player.");
                        break;
                     case "not-muted":
                        punisher.sendMessage(CC.RED + "Player not muted.");
                        break;
                     case "not-banned":
                        punisher.sendMessage(CC.RED + "Player not banned.");
                        break;
                     case "success":
                        String broadcast = null;
                        Mineman mineman = targetPlayer != null ? PunishmentManager.this.plugin.getPlayerManager().getPlayer(targetPlayer.getUniqueId()) : null;
                        switch (type) {
                           case BLACKLIST:
                              broadcast = "blacklisted";
                              if (targetPlayer != null) {
                                 targetPlayer.kickPlayer(StringUtil.BLACKLIST);
                              } else if (server != null) {
                                 PunishmentManager.this.plugin.getCoreRedisManager().kickPlayer(target, server, StringUtil.BLACKLIST);
                              }
                              break;
                           case UNBLACKLIST:
                              broadcast = "unblacklisted";
                              if (mineman != null) {
                                 mineman.setBlacklisted(false);
                                 mineman.setBanTime(new Timestamp(0L));
                                 mineman.setBanData(new BanWrapper("", false));
                              }
                              break;
                           case IPBAN:
                              broadcast = "banned";
                              if (targetPlayer != null) {
                                 targetPlayer.kickPlayer(StringUtil.IP_BAN);
                              } else if (server != null) {
                                 PunishmentManager.this.plugin.getCoreRedisManager().kickPlayer(target, server, StringUtil.IP_BAN);
                              }
                              break;
                           case BAN:
                              broadcast = temporary ? "temporarily banned" : "banned";
                              if (targetPlayer != null) {
                                 targetPlayer.kickPlayer(temporary ? String.format(StringUtil.TEMPORARY_BAN, finalExpiryTime) : StringUtil.PERMANENT_BAN);
                              } else if (server != null) {
                                 PunishmentManager.this.plugin.getCoreRedisManager().kickPlayer(target, server, temporary ? String.format(StringUtil.TEMPORARY_BAN, finalExpiryTime) : StringUtil.PERMANENT_BAN);
                              }
                              break;
                           case KICK:
                              broadcast = "kicked";
                              if (targetPlayer != null) {
                                 targetPlayer.kickPlayer(CC.RED + "You were kicked: " + reason);
                              } else if (server != null) {
                                 PunishmentManager.this.plugin.getCoreRedisManager().kickPlayer(target, server, CC.RED + "You were kicked " + reason);
                              }
                              break;
                           case UNBAN:
                              broadcast = "unbanned";
                              if (mineman != null) {
                                 mineman.setBanned(false);
                                 mineman.setBanTime(new Timestamp(0L));
                                 mineman.setBanData(new BanWrapper("", false));
                              }
                              break;
                           case UNMUTE:
                              broadcast = "unmuted";
                              if (mineman != null) {
                                 mineman.setMuted(false);
                                 mineman.setMuteTime(new Timestamp(0L));
                              }
                              break;
                           case MUTE:
                              broadcast = temporary ? "temporarily muted" : "muted";
                              if (mineman != null) {
                                 mineman.setMuted(true);
                                 mineman.setMuteTime(expiry);
                              } else if (server != null) {
                                 PunishmentManager.this.plugin.getCoreRedisManager().mutePlayer(target, server, broadcast, expiry);
                              }
                        }

                        if (!type.getName().startsWith("un")) {
                           PunishmentManager.this.broadcastPunishment(broadcast, reason, target, punisher.getName(), silent);
                        } else {
                           PunishmentManager.this.broadcastPunishment(broadcast, reason, target, punisher.getName(), false);
                        }
                        break;
                     default:
                        punisher.sendMessage(CC.RED + "An error has occurred. Please notify an administrator.");
                        PunishmentManager.this.plugin.getLogger().warning("Punishment returned: " + element);
                  }

               }

               public void onError(String message) {
                  super.onError(message);
                  punisher.sendMessage(MessageFormat.format("{0}Something went wrong while punishing ''{1}''.", CC.RED, target));
               }
            });
         });
      }
   }

   @ConstructorProperties({"plugin"})
   public PunishmentManager(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
