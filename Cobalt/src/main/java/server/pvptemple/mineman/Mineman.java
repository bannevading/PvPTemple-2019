package server.pvptemple.mineman;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.beans.ConstructorProperties;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.GlobalRequest;
import server.pvptemple.api.impl.IPCheckRequest;
import server.pvptemple.api.impl.IgnoreRequest;
import server.pvptemple.api.impl.JoinRequest;
import server.pvptemple.event.player.MinemanRetrieveEvent;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.BanWrapper;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.StringUtil;
import server.pvptemple.util.finalutil.TimeUtil;

public class Mineman {
   @Getter
   private final Set<Integer> ignoring = new HashSet();
   @Getter
   private final UUID uuid;
   @Getter
   private final String name;
   @Getter
   private final InetAddress ipAddress;
   @Getter
   @Setter
   private Rank rank;
   @Getter
   @Setter
   private Rank disguiseRank;
   @Getter
   @Setter
   private BanWrapper banData;
   @Getter
   @Setter
   private Timestamp muteTime;
   @Getter
   @Setter
   private Timestamp banTime;
   @Getter
   @Setter
   private String lastConversation;
   @Getter
   @Setter
   private String customColor;
   @Getter
   @Setter
   private String reportingPlayer;
   @Setter
   private ChatType chatType;
   @Getter
   @Setter
   private long reportCooldown;
   @Getter
   @Setter
   private long commandCooldown;
   @Getter
   @Setter
   private long chatCooldown;
   @Getter
   @Setter
   private long silentSpam;
   @Getter
   @Setter
   private boolean canSeeMessages;
   @Getter
   @Setter
   private boolean canSeeStaffMessages;
   @Getter
   @Setter
   private boolean chatEnabled;
   @Getter
   @Setter
   private boolean errorLoadingData;
   @Getter
   @Setter
   private boolean blacklisted;
   @Getter
   @Setter
   private boolean dataLoaded;
   @Getter
   @Setter
   private boolean vanishMode;
   @Getter
   @Setter
   private boolean ipBanned;
   @Getter
   @Setter
   private boolean banned;
   @Getter
   @Setter
   private boolean muted;
   @Getter
   @Setter
   private long lastRegister;
   @Getter
   @Setter
   private int id;
   @Getter
   @Setter
   private int filter;
   @Getter
   @Setter
   private int spam;
   @Getter
   @Setter
   private int dataLoadStage;

   public boolean isIgnoring(int id) {
      return this.ignoring.contains(id);
   }

   public boolean toggleIgnore(int id) {
      if (!this.ignoring.remove(id)) {
         this.ignoring.add(id);
         return true;
      } else {
         return false;
      }
   }

   public BanWrapper fetchData() {
      JsonElement data = CorePlugin.getInstance().getRequestProcessor().sendRequest(new GlobalRequest(this.ipAddress, this.uuid, this.name));
      this.banData = this.parsePlayerData(data);
      if (!this.isBanned() && !this.isIpBanned()) {
         for(JsonElement ignoreData : CorePlugin.getInstance().getRequestProcessor().sendRequest(new IgnoreRequest(this.uuid)).getAsJsonArray()) {
            JsonObject object = ignoreData.getAsJsonObject();
            this.ignoring.add(object.get("ignoredId").getAsInt());
         }
      }

      this.dataLoaded = true;
      CorePlugin.getInstance().getServer().getPluginManager().callEvent(new MinemanRetrieveEvent(this, this.banData));
      return this.banData;
   }

   private BanWrapper parsePlayerData(JsonElement element) {
      JsonObject object = element.getAsJsonObject();
      JsonElement idElement = object.get("playerId");
      this.id = idElement.getAsInt();
      String rank = object.get("rank").getAsString();
      if (rank != null) {
         try {
            this.rank = Rank.getByName(rank);
         } catch (Exception var9) {
            this.rank = Rank.NORMAL;
         }
      } else {
         this.rank = Rank.NORMAL;
      }

      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      JsonElement element1 = object.get("chatColor");
      if (!element1.isJsonNull() && !element1.getAsString().isEmpty()) {
         this.customColor = '§' + element1.getAsString();
      }

      element1 = object.get("muted");
      if (!element1.isJsonNull()) {
         this.muted = element1.getAsBoolean();
      }

      element1 = object.get("muteTime");
      if (!element1.isJsonNull()) {
         long muteTime = element1.getAsLong();
         this.muteTime = new Timestamp(muteTime);
         if (this.muteTime.before(currentTime)) {
            this.muteTime = null;
            this.muted = false;
         } else {
            this.muted = true;
         }
      }

      element1 = object.get("blacklisted");
      if (!element1.isJsonNull()) {
         this.blacklisted = element1.getAsBoolean();
      }

      element1 = object.get("ipBanned");
      if (!element1.isJsonNull()) {
         this.ipBanned = element1.getAsBoolean();
      }

      element1 = object.get("banned");
      if (!element1.isJsonNull()) {
         this.banned = element1.getAsBoolean();
      }

      element1 = object.get("banTime");
      if (!element1.isJsonNull()) {
         long banTime = element1.getAsLong();
         this.banTime = new Timestamp(banTime);
         if (this.banTime.before(currentTime)) {
            this.ipBanned = false;
            this.banTime = null;
            this.banned = false;
         } else {
            this.banned = true;
         }
      }

      BanWrapper wrapper = this.checkIPBan();
      if (wrapper != null) {
         return wrapper;
      } else {
         Timestamp now = new Timestamp(System.currentTimeMillis());
         if (this.blacklisted) {
            return new BanWrapper(StringUtil.BLACKLIST, true);
         } else if (this.banned && this.banTime == null) {
            return new BanWrapper(StringUtil.PERMANENT_BAN, true);
         } else if (this.banned && this.banTime != null && this.banTime.after(now)) {
            return new BanWrapper(String.format(StringUtil.TEMPORARY_BAN, TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - this.banTime.getTime()))), this.banTime.after(now));
         } else {
            return this.ipBanned ? new BanWrapper(StringUtil.IP_BAN, true) : new BanWrapper("", false);
         }
      }
   }

   public void onJoin() {
      if (this.hasRank(Rank.TRAINEE)) {
         CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            String message = CC.DARK_GRAY + "[" + CC.GOLD + "Staff" + CC.DARK_GRAY + "] " + this.getRank().getColor() + this.getPlayer().getName() + CC.YELLOW + " has joined " + CC.GOLD + (CorePlugin.getInstance().getConfig().getBoolean("serverdata.nice-name-global-chat") ? StringUtil.toNiceString(CorePlugin.getInstance().getServerManager().getServerName()) : CorePlugin.getInstance().getServerManager().getServerName()) + CC.YELLOW + ".";
            JsonObject data = new JsonObject();
            data.addProperty("rank", this.getRank().name());
            CorePlugin.getInstance().getCoreRedisManager().sendMessage(message, Rank.TRAINEE, data);
         });
      }

      this.setupName();
      PermissionAttachment attachment = this.getPlayer().addAttachment(CorePlugin.getInstance());
      switch (this.rank) {
         case BASIC:
            attachment.setPermission("perm.basic", true);
            break;
         case PRIME:
            attachment.setPermission("perm.prime", true);
            break;
         case ELITE:
            attachment.setPermission("perm.elite", true);
            break;
         case MASTER:
            attachment.setPermission("perm.master", true);
            break;
         case YOUTUBER:
            attachment.setPermission("perm.youtuber", true);
            break;
         case FAMOUS:
            attachment.setPermission("perm.famous", true);
            break;
         case PARTNER:
            attachment.setPermission("perm.partner", true);
      }

      if (this.hasRank(Rank.NORMAL)) {
         attachment.setPermission("minecraft.command.me", false);
         attachment.setPermission("minecraft.command.tell", false);
         attachment.setPermission("minecraft.command.tell", true);
         attachment.setPermission("hcf.command.lives", true);
         attachment.setPermission("hcf.command.lives.argument.check", true);
         attachment.setPermission("hcf.command.lives.argument.give", true);
         attachment.setPermission("hcf.command.lives.argument.revive", true);
         attachment.setPermission("hcf.command.koth", true);
         attachment.setPermission("hcf.command.koth.argument.help", true);
         attachment.setPermission("hcf.command.koth.argument.next", true);
         attachment.setPermission("hcf.command.koth.argument.schedule", true);
         attachment.setPermission("hcf.command.economy", true);
         attachment.setPermission("hcf.command.pay", true);
         attachment.setPermission("hcf.command.logout", true);
         attachment.setPermission("hcf.command.pvptimer", true);
         attachment.setPermission("hcf.command.playertime", true);
         attachment.setPermission("sidebar.lines", true);
         attachment.setPermission("hcf.command.mapkit", true);
         attachment.setPermission("hcf.command.list", true);
         attachment.setPermission("hcf.command.ores", true);
         attachment.setPermission("hcf.command.blockfilter", true);
      }

      if (this.hasRank(Rank.MASTER)) {
         attachment.setPermission("hcf.command.nightvision", true);
      }

      if (this.hasRank(Rank.TRAINEE)) {
         attachment.setPermission("hcf.command.revive", true);
         attachment.setPermission("hcf.deathban.bypass", true);
         attachment.setPermission("hcf.command.playertime.viewstaff", true);
         attachment.setPermission("hcf.bypassanticommandtab", true);
         attachment.setPermission("hcf.bypass.syntaxblocked", true);
         attachment.setPermission("server.staff", true);
         attachment.setPermission("hcf.bypass.syntaxblocked", true);
         attachment.setPermission("hcf.bypass.syntaxblocked", true);
         attachment.setPermission("hcf.bypass.syntaxblocked", true);
      }

      if (this.hasRank(Rank.MOD)) {
         attachment.setPermission("hcf.command.faction.argument.setdtr", true);
         attachment.setPermission("hcf.command.faction.argument.setdtrregen", true);
         attachment.setPermission("hcf.command.lives.argument.checkdeathban", true);
         attachment.setPermission("hcf.command.inv", true);
      }

      if (this.hasRank(Rank.ADMIN)) {
         attachment.setPermission("worldedit.*", true);
         attachment.setPermission("hcf.command.event", true);
         attachment.setPermission("hcf.command.conquest", true);
         attachment.setPermission("hcf.command.fury", true);
         attachment.setPermission("hcf.command.event", true);
         attachment.setPermission("hcf.command.event", true);
         attachment.setPermission("hcf.command.event", true);
         attachment.setPermission("hcf.command.koth.argument.setcapdelay", true);
         attachment.setPermission("hcf.economy.take", true);
         attachment.setPermission("hcf.economy.set", true);
         attachment.setPermission("hcf.command.timer", true);
         attachment.setPermission("hcf.faction.protection.bypass", true);
         attachment.setPermission("hcf.command.toggleend", true);
         attachment.setPermission("hcf.command.loadout", true);
         attachment.setPermission("hcf.command.mountain", true);
      }

      if (this.hasRank(Rank.SENIORADMIN)) {
         attachment.setPermission("hcf.command.faction.argument.forcedemote", true);
         attachment.setPermission("hcf.command.faction.argument.forcejoin", true);
         attachment.setPermission("hcf.command.faction.argument.forcekick", true);
         attachment.setPermission("hcf.command.faction.argument.forceleader", true);
         attachment.setPermission("hcf.command.faction.argument.forcepromote", true);
         attachment.setPermission("hcf.command.faction.argument.forcename", true);
         attachment.setPermission("hcf.command.faction.argument.forceunclaimhere", true);
         attachment.setPermission("hcf.command.lives.argument.set", true);
         attachment.setPermission("hcf.command.lives.argument.setdeathbantime", true);
      }

      if (this.hasRank(Rank.PLATFORMADMIN)) {
         attachment.setPermission("hcf.command.faction.argument.ban", true);
         attachment.setPermission("hcf.command.faction.argument.claimfor", true);
         attachment.setPermission("hcf.command.faction.argument.clearclaims", true);
         attachment.setPermission("hcf.command.faction.argument.mute", true);
         attachment.setPermission("hcf.command.faction.argument.remove", true);
         attachment.setPermission("hcf.command.faction.argument.setdeathbanmultiplier", true);
         attachment.setPermission("hcf.command.lives.argument.cleardeathbans", true);
         attachment.setPermission("hcf.command.sotw", true);
         attachment.setPermission("hcf.command.eotw", true);
         attachment.setPermission("hcf.command.savedata", true);
         attachment.setPermission("freezeserver.freeze", true);
         attachment.setPermission("freezeserver.bypass", true);
         attachment.setPermission("hcf.command.freezeserver", true);
      }

      if (this.hasRank(Rank.OWNER)) {
         attachment.setPermission("hcf.command.faction.argument.reload", true);
      }

      CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new JoinRequest(this.ipAddress, this.uuid, this.name));
   }

   public Player getPlayer() {
      return CorePlugin.getInstance().getServer().getPlayer(this.uuid);
   }

   public boolean hasRank(Rank rank) {
      return this.rank.hasRank(rank);
   }

   private BanWrapper checkIPBan() {
      Timestamp now = new Timestamp(System.currentTimeMillis());
      if (!this.banned && !this.ipBanned && (this.banTime == null || !this.banTime.after(now))) {
         JsonElement data = CorePlugin.getInstance().getRequestProcessor().sendRequest(new IPCheckRequest(this.ipAddress, this.uuid));
         if (data.getAsBoolean()) {
            this.ipBanned = true;
            Player player = this.getPlayer();
            if (player == null) {
               return new BanWrapper(StringUtil.IP_BAN_OTHER, true);
            }

            player.kickPlayer(StringUtil.IP_BAN_OTHER);
         }

         return null;
      } else {
         return null;
      }
   }

   public ChatType getChatType() {
      return this.chatType == null ? ChatType.NORMAL : this.chatType;
   }

   public String getDisplayName() {
      return this.getPlayer().getDisplayName();
   }

   public Rank getDisplayRank() {
      return this.disguiseRank != null ? this.disguiseRank : this.rank;
   }

   void setupName() {
      Player player = Bukkit.getPlayer(this.name);
      if (player != null) {
         Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
         if (mineman != null) {
            String color = mineman.getDisplayRank().getColor();
            if (!player.getDisplayName().equals(color + player.getName())) {
               player.setDisplayName(color + player.getName());
            }
         }
      }

   }

   public void updateTabList(Rank rank) {
      this.getPlayer().setPlayerListName(rank.getColor() + this.getDisplayName() + CC.R);
   }

   @ConstructorProperties({"uuid", "name", "ipAddress"})
   public Mineman(UUID uuid, String name, InetAddress ipAddress) {
      this.rank = Rank.NORMAL;
      this.canSeeMessages = true;
      this.canSeeStaffMessages = true;
      this.chatEnabled = true;
      this.uuid = uuid;
      this.name = name;
      this.ipAddress = ipAddress;
   }

   public static enum ChatType {
      NORMAL,
      STAFF,
      HOST,
      DEV;
   }
}
