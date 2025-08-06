package server.pvptemple.util.tab;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import com.viaversion.viaversion.api.Via;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.WorldSettings;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;
import server.pvptemple.util.finalutil.Color;

public class TabManager {
   private static LayoutProvider layoutProvider;
   private static Map<String, TabManager> tabs = new ConcurrentHashMap<>();
   private Player player;
   private TabLayout initialLayout;
   private StringBuilder removeColorCodesBuilder = new StringBuilder();
   private Map<String, GameProfile> cache = new ConcurrentHashMap<>();
   private Map<String, String> previousNames = new HashMap<>();
   private Map<String, Integer> previousPings = new HashMap<>();
   private Set<String> createdTeams = new HashSet<>();
   private AtomicReference<Object> defaultPropertyMap = new AtomicReference<>();
   private PropertyMap defaultSkin;
   private boolean initiatedTab = false;

   public TabManager() {
      (new TabThread()).start();
   }

   public TabManager(Player player) {
      this.player = player;
   }

   public static void addPlayer(Player player) {
      tabs.put(player.getName(), new TabManager(player));
   }

   public static void updatePlayer(Player player) {
      if (tabs.containsKey(player.getName())) {
         ((TabManager)tabs.get(player.getName())).update();
      }

   }

   public static void removePlayer(Player player) {
      tabs.remove(player.getName());
   }

   public static void toggleTab(Player player) {
      PlayerData data = Carbon.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
      if (data.isTab()) {
         ((TabManager)tabs.get(player.getName())).reset();
         data.setTab(false);
      } else {
         data.setTab(true);
      }

      player.sendMessage(Color.translate((data.isTab() ? "&a" : "&c") + "You have " + (data.isTab() ? "enabled" : "disabled") + " tab list."));
   }

   private void init() {
      if (!this.initiatedTab) {
         TabLayout initialLayout = TabLayout.createEmpty(this.player);
         if (!initialLayout.is18()) {
            Bukkit.getOnlinePlayers().forEach((player) -> this.updateTabList(player.getName(), 0, ((CraftPlayer)player).getProfile(), EnumPlayerInfoAction.REMOVE_PLAYER));
         }

         Stream.of(initialLayout.getTabNames()).forEach((tabNames) -> {
            this.updateTabList(tabNames, 0, EnumPlayerInfoAction.ADD_PLAYER);
            String teamName = tabNames.replaceAll("§", "");
            if (!this.createdTeams.contains(teamName)) {
               this.createAndAddMember(teamName, tabNames);
               this.createdTeams.add(teamName);
            }

         });
         this.initialLayout = initialLayout;
         this.initiatedTab = true;
      }

   }

   protected void update() {
      if (layoutProvider != null) {
         TabLayout layout = layoutProvider.getLayout(this.player);
         if (layout == null) {
            if (this.initiatedTab) {
               this.reset();
            }

            return;
         }

         this.init();

         for(int y = 0; y < TabLayout.HEIGHT; ++y) {
            for(int x = 0; x < TabLayout.WIDTH; ++x) {
               String entry = layout.getStringAt(x, y);
               int ping = layout.getPingAt(x, y);
               String entryName = this.initialLayout.getStringAt(x, y);
               this.removeColorCodesBuilder.setLength(0);
               this.removeColorCodesBuilder.append(entryName);
               int j = 0;

               for(int i = 0; i < this.removeColorCodesBuilder.length(); ++i) {
                  if (167 != this.removeColorCodesBuilder.charAt(i)) {
                     this.removeColorCodesBuilder.setCharAt(j++, this.removeColorCodesBuilder.charAt(i));
                  }
               }

               this.removeColorCodesBuilder.delete(j, this.removeColorCodesBuilder.length());
               String teamName = "$" + this.removeColorCodesBuilder.toString();
               if (this.previousNames.containsKey(entryName)) {
                  if (!((String)this.previousNames.get(entryName)).equals(entry)) {
                     this.update(entryName, teamName, entry, ping);
                  } else if (this.previousPings.containsKey(entryName) && (Integer)this.previousPings.get(entryName) != ping) {
                     this.updateTabList(entryName, ping, EnumPlayerInfoAction.UPDATE_LATENCY);
                     this.previousPings.put(entryName, ping);
                  }
               } else {
                  this.update(entryName, teamName, entry, ping);
               }
            }
         }
      }

   }

   private void reset() {
      this.initiatedTab = false;
      Stream.of(this.initialLayout.getTabNames()).forEach((names) -> this.updateTabList(names, 0, EnumPlayerInfoAction.REMOVE_PLAYER));
      EntityPlayer entityPlayer = ((CraftPlayer)this.player).getHandle();
      this.updateTabList(this.player.getPlayerListName(), entityPlayer.ping, entityPlayer.getProfile(), EnumPlayerInfoAction.ADD_PLAYER);
      int[] count = new int[]{1};
      Bukkit.getOnlinePlayers().stream().filter((player) -> this.player != player).forEach((player) -> {
         if (count[0] < this.initialLayout.getTabNames().length - 1) {
            EntityPlayer online = ((CraftPlayer)player).getHandle();
            this.updateTabList(player.getPlayerListName(), online.ping, online.getProfile(), EnumPlayerInfoAction.ADD_PLAYER);
            int var10002 = count[0]++;
         }

      });
   }

   private void update(String entryName, String teamName, String entry, int ping) {
      String[] entryStrings = this.split(entry);
      String prefix = entryStrings[0];
      String suffix = entryStrings[1];
      if (!suffix.isEmpty()) {
         if (prefix.charAt(prefix.length() - 1) == 167) {
            prefix = prefix.substring(0, prefix.length() - 1);
            suffix = '§' + suffix;
         }

         String suffixPrefix = ChatColor.RESET.toString();
         if (!ChatColor.getLastColors(prefix).isEmpty()) {
            suffixPrefix = ChatColor.getLastColors(prefix);
         }

         suffix = suffix.length() <= 14 ? suffixPrefix + suffix : suffixPrefix + suffix.substring(0, 14);
      }

      this.updateScore(teamName, prefix, suffix);
      this.updateTabList(entryName, ping, EnumPlayerInfoAction.UPDATE_LATENCY);
      this.previousNames.put(entryName, entry);
      this.previousPings.put(entryName, ping);
   }

   private PropertyMap fetchSkin() {
      GameProfile profile = new GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "bananasquad");
      HttpAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
      MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
      GameProfile gameProfile = sessionService.fillProfileProperties(profile, true);
      return gameProfile.getProperties();
   }

   public static boolean is18(Player player) {
      return Via.getAPI().getPlayerVersion(player.getUniqueId()) == 47;
   }

   private GameProfile getOrCreateProfile(String name, UUID id) {
      return this.cache.computeIfAbsent(name, (n) -> new GameProfile(id, n));
   }

   private GameProfile getOrCreateProfile(String name) {
      return this.getOrCreateProfile(name, new UUID((new Random()).nextLong(), (new Random()).nextLong()));
   }

   private PropertyMap getDefaultPropertyMap() {
      Object value = this.defaultPropertyMap.get();
      if (value == null) {
         synchronized(this.defaultPropertyMap) {
            value = this.defaultPropertyMap.get();
            if (value == null) {
               PropertyMap actualValue = this.defaultSkin;
               value = actualValue == null ? this.defaultPropertyMap : actualValue;
               this.defaultPropertyMap.set(value);
            }
         }
      }

      return (PropertyMap)(value == this.defaultPropertyMap ? null : value);
   }

   private void createAndAddMember(String name, String member) {
      this.sendTeamPacketMod("$" + name, "", "", Collections.singletonList(member), 0);
   }

   private void updateScore(String score, String prefix, String suffix) {
      this.sendTeamPacketMod(score, prefix, suffix, null, 2);
   }

   private void updateTabList(String name, int ping, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
      this.updateTabList(name, ping, this.getOrCreateProfile(name), action);
   }

   private void updateTabList(String name, int ping, GameProfile profile, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
      this.sendPacketMod(this.player, action, profile, ping, EnumGamemode.SURVIVAL, ChatSerializer.a(name));
   }

   private String[] split(String line) {
      return line.length() <= 16 ? new String[]{line, ""} : new String[]{line.substring(0, 16), line.substring(16, line.length())};
   }

   private void sendPacketMod(Player player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, GameProfile profile, int ping, WorldSettings.EnumGamemode gamemode, IChatBaseComponent name) {
      PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action);
      packet.b.add(packet.new PlayerInfoData(profile, ping, gamemode, name));
      ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
   }

   private void sendTeamPacketMod(String name, String prefix, String suffix, Collection<String> players, int i) {
      PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
      packet.a = name;
      packet.h = i;
      if (i == 0 || i == 2) {
         packet.b = name;
         packet.c = prefix;
         packet.d = suffix;
         packet.i = 1;
      }

      if (i == 0) {
         packet.g.addAll(players);
      }

      ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket(packet);
   }

   public Player getPlayer() {
      return this.player;
   }

   public TabLayout getInitialLayout() {
      return this.initialLayout;
   }

   public StringBuilder getRemoveColorCodesBuilder() {
      return this.removeColorCodesBuilder;
   }

   public Map<String, GameProfile> getCache() {
      return this.cache;
   }

   public Map<String, String> getPreviousNames() {
      return this.previousNames;
   }

   public Map<String, Integer> getPreviousPings() {
      return this.previousPings;
   }

   public Set<String> getCreatedTeams() {
      return this.createdTeams;
   }

   public PropertyMap getDefaultSkin() {
      return this.defaultSkin;
   }

   public boolean isInitiatedTab() {
      return this.initiatedTab;
   }

   public static LayoutProvider getLayoutProvider() {
      return layoutProvider;
   }

   public static void setLayoutProvider(LayoutProvider layoutProvider) {
      TabManager.layoutProvider = layoutProvider;
   }
}
