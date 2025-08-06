package server.pvptemple.redis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.redis.JedisPublisher;
import server.pvptemple.redis.JedisSubscriber;
import server.pvptemple.redis.subscription.JedisSubscriptionHandler;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;

public class CoreRedisManager {
   private static final Pattern CLICKABLE_PATTERN = Pattern.compile("(.*)(\\{clickable::command=\"(.*)\"})(.*)(\\{/clickable})(.*)");
   private final server.pvptemple.redis.JedisSubscriber<JsonObject> messagesSubscriber;
   private final server.pvptemple.redis.JedisPublisher<JsonObject> messagesPublisher;
   private final CorePlugin plugin;

   public CoreRedisManager(CorePlugin plugin) {
      this.plugin = plugin;
      this.messagesSubscriber = new JedisSubscriber<JsonObject>(this.plugin.getJedisConfig().toJedisSettings(), "global-messages", JsonObject.class, new GlobalMessageSubscriptionHandler());
      this.messagesPublisher = new JedisPublisher<JsonObject>(this.plugin.getJedisConfig().toJedisSettings(), "global-messages");
   }

   public static String getServerMessagePrefix() {
      return CC.GRAY + "(" + (CorePlugin.getInstance().getConfig().getBoolean("serverdata.nice-name-global-chat") ? StringUtil.toNiceString(CorePlugin.getInstance().getServerManager().getServerName()) : CorePlugin.getInstance().getServerManager().getServerName()) + CC.GRAY + ") ";
   }

   public static boolean handleMessage(Mineman mineman, String message) {
      return handleMessage(mineman, message, mineman.getChatType());
   }

   public static boolean handleMessage(Mineman mineman, String message, Mineman.ChatType chatType) {
      Rank rank = mineman.getRank();
      String suffix = rank.getColor() + mineman.getName() + CC.RESET + ": " + message;
      if (chatType == Mineman.ChatType.STAFF) {
         CorePlugin.getInstance().getCoreRedisManager().sendMessage(getServerMessagePrefix() + CC.DARK_GRAY + "[" + CC.GOLD + "Staff" + CC.DARK_GRAY + "] " + suffix, Rank.TRAINEE);
         return true;
      } else if (chatType == Mineman.ChatType.DEV) {
         CorePlugin.getInstance().getCoreRedisManager().sendMessage(getServerMessagePrefix() + CC.DARK_GRAY + "[" + CC.GOLD + "Dev" + CC.DARK_GRAY + "] " + suffix, Rank.DEVELOPER);
         return true;
      } else if (chatType == Mineman.ChatType.HOST) {
         CorePlugin.getInstance().getCoreRedisManager().sendMessage(getServerMessagePrefix() + CC.DARK_GRAY + "[" + CC.GOLD + "Host" + CC.DARK_GRAY + "] " + suffix, Rank.HOST);
         return true;
      } else {
         return false;
      }
   }

   private static BaseComponent[] parseMessage(String message) {
      Clickable clickable = new Clickable();
      Matcher clickableMatcher = CLICKABLE_PATTERN.matcher(message);
      if (clickableMatcher.matches()) {
         clickable.add(clickableMatcher.group(1));
         clickable.add(clickableMatcher.group(4), (String)null, clickableMatcher.group(3));
         clickable.add(clickableMatcher.group(6));
      } else {
         clickable.add(message);
      }

      return clickable.asComponents();
   }

   public void broadcastGloballyClickable(String message, String hover, String command) {
      this.broadcastClickable(message, "clickable", hover, command);
   }

   public void broadcastClickable(String message, String type, String hover, String command) {
      JsonObject jsonObject = this.generateBaseMessage(message);
      if (type != null) {
         jsonObject.addProperty("type", type);
      }

      if (hover != null) {
         jsonObject.addProperty("hover", hover);
      }

      if (command != null) {
         jsonObject.addProperty("command", command);
      }

      this.messagesPublisher.write(jsonObject);
   }

   public void broadcastGlobally(String message) {
      this.broadcast(message, "global", (String)null);
   }

   public void kickPlayer(String player, String server, String message) {
      this.punishPlayer(player, server, message, "kick");
   }

   public void mutePlayer(String player, String server, String message, Timestamp expiry) {
      this.punishPlayer(player, server, message, "mute", new BasicNameValuePair("time", String.valueOf(expiry == null ? null : expiry.getTime())));
   }

   private void punishPlayer(String player, String server, String message, String type, BasicNameValuePair... args) {
      JsonObject jsonObject = this.generateBaseMessage(message);
      jsonObject.addProperty("type", type);
      jsonObject.addProperty("player", player);
      jsonObject.addProperty("server", server);

      for(BasicNameValuePair arg : args) {
         jsonObject.addProperty(arg.getName(), arg.getValue());
      }

      this.messagesPublisher.write(jsonObject);
   }

   public void broadcastServer(String message, String regex) {
      this.broadcast(message, "server", regex);
   }

   public void broadcast(String message, String type, String server) {
      JsonObject jsonObject = this.generateBaseMessage(message);
      if (type != null) {
         jsonObject.addProperty("type", type);
      }

      if (server != null) {
         jsonObject.addProperty("server", server);
      }

      this.messagesPublisher.write(jsonObject);
   }

   private JsonObject generateBaseMessage(String message) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("server-id", this.plugin.getServerManager().getServerName());
      jsonObject.addProperty("message", message);
      return jsonObject;
   }

   public void sendMessage(String message, Rank rank, JsonObject append) {
      JsonObject jsonObject = this.generateBaseMessage(message);
      jsonObject.addProperty("rank", rank.getName());
      if (append != null) {
         jsonObject.add("extra", append);
      }

      this.messagesPublisher.write(jsonObject);
   }

   public void sendMessage(String message, Rank fromRank, String from, UUID target, boolean filter) {
      JsonObject jsonObject = this.generateBaseMessage(message);
      jsonObject.addProperty("from", from);
      jsonObject.addProperty("from-rank", fromRank.getName());
      jsonObject.addProperty("target", target.toString());
      jsonObject.addProperty("type", "direct-message");
      jsonObject.addProperty("filter", filter);
      this.messagesPublisher.write(jsonObject);
   }

   public void sendMessage(String message, Rank rank) {
      this.sendMessage(message, rank, (JsonObject)null);
   }

   class GlobalMessageSubscriptionHandler implements JedisSubscriptionHandler<JsonObject> {
      public void handleMessage(JsonObject object) {
         String serverId = object.get("server-id").getAsString();
         String message = ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString());
         if (object.get("rank") != null) {
            Rank rank = Rank.getByName(object.get("rank").getAsString());
            PlayerUtil.messageRank(message, rank);
         } else {
            JsonElement jsonElement = object.get("type");
            if (jsonElement != null) {
               String type = jsonElement.getAsString();
               switch (type.toLowerCase()) {
                  case "global":
                     CoreRedisManager.this.plugin.getServer().broadcastMessage(message);
                     break;
                  case "clickable":
                     TextComponent click = new TextComponent(message);
                     click.setClickEvent(new ClickEvent(Action.RUN_COMMAND, object.get("command").getAsString()));
                     click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(Color.translate(object.get("hover").getAsString()))).create()));
                     Bukkit.getOnlinePlayers().forEach((o) -> o.sendMessage(click));
                     break;
                  case "kick":
                     jsonElement = object.get("target");
                     if (jsonElement != null) {
                        String target = jsonElement.getAsString();
                        Player player = CoreRedisManager.this.plugin.getServer().getPlayer(target);
                        if (player != null) {
                           player.kickPlayer(message);
                        }
                     }
                     break;
                  case "mute":
                     jsonElement = object.get("target");
                     if (jsonElement != null) {
                        String target = jsonElement.getAsString();
                        Player player = CoreRedisManager.this.plugin.getServer().getPlayer(target);
                        if (player != null) {
                           Mineman mineman = CoreRedisManager.this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
                           Timestamp expiry = null;
                           jsonElement = object.get("time");
                           if (!jsonElement.isJsonNull()) {
                              expiry = new Timestamp(Long.parseLong(jsonElement.getAsString()));
                           }

                           if (mineman != null) {
                              mineman.setMuteTime(expiry);
                              mineman.setMuted(true);
                           }

                           String send = CC.RED + "You have been muted for " + message;
                           player.sendMessage(send);
                        }
                     }
                     break;
                  case "server":
                     jsonElement = object.get("server");
                     if (jsonElement != null) {
                        String server = jsonElement.getAsString();
                        if (CorePlugin.getInstance().getServerManager().getServerName().matches(server)) {
                           BaseComponent[] components = CoreRedisManager.parseMessage(message);

                           for(Player player : CoreRedisManager.this.plugin.getServer().getOnlinePlayers()) {
                              player.sendMessage(components);
                           }
                        }
                     }
                     break;
                  case "direct-message":
                     jsonElement = object.get("target");
                     if (jsonElement != null) {
                        String targetUuid = jsonElement.getAsString();
                        UUID uuid = UUID.fromString(targetUuid);
                        Player target = CorePlugin.getInstance().getServer().getPlayer(uuid);
                        Mineman targetMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(uuid);
                        if (target != null) {
                           String from = object.get("from").getAsString();
                           if (!object.get("filter").getAsBoolean()) {
                              Rank fromRank = Rank.getByName(object.get("from-rank").getAsString());
                              if (targetMineman.isCanSeeMessages() && !targetMineman.isIgnoring(targetMineman.getId()) || fromRank.hasRank(Rank.TRAINEE)) {
                                 String formatted = StringUtil.formatPrivateMessage(targetMineman.getRank().getColor() + target.getName(), fromRank.getColor() + from, message)[1];
                                 target.sendMessage(formatted);
                                 targetMineman.setLastConversation(from);
                              }
                           }

                           JsonObject jsonObject = CoreRedisManager.this.generateBaseMessage(message);
                           jsonObject.addProperty("from", from);
                           jsonObject.addProperty("target-name", target.getName());
                           jsonObject.addProperty("type", "direct-message-received");
                           jsonObject.addProperty("target-rank", targetMineman.getRank().getColor());
                           CoreRedisManager.this.messagesPublisher.write(jsonObject);
                        }
                     }
                     break;
                  case "direct-message-received":
                     String from = object.get("from").getAsString();
                     Player player = CorePlugin.getInstance().getServer().getPlayer(from);
                     if (player != null) {
                        String name = object.get("target-name").getAsString();
                        String rank = object.get("target-rank").getAsString();
                        String formatted = StringUtil.formatPrivateMessage(rank + name, player.getName(), message)[0];
                        player.sendMessage(formatted);
                        Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
                        mineman.setLastConversation(name);
                     }
               }
            } else {
               CoreRedisManager.this.plugin.getLogger().warning("Unknown message type for global message from " + serverId);
            }
         }

      }
   }
}
