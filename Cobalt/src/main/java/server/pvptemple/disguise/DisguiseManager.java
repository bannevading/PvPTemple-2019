package server.pvptemple.disguise;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import server.pvptemple.CorePlugin;
import server.pvptemple.disguise.UpdateSkinTask;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.other.GameProfileUtil;
import server.pvptemple.util.other.UUIDFetcher;

public class DisguiseManager {
   public static final String API_UID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
   public static final String API_UID_NAME = "name";
   public static final String API_UID_PROPERTIES = "properties";
   public static final String API_UID_SIGNATURE = "signature";
   public static final String API_UID_VALUE = "value";
   private final CorePlugin plugin;
   private final Map<String, GameProfile> skinCache = new HashMap();
   @Getter
   private final Map<UUID, GameProfile> originalCache = new HashMap();
   @Getter
   private final Map<UUID, DisguiseData> disguiseData = new HashMap();
   @Getter
   private Predicate<Player> allowedDisguising = (player) -> {
      player.sendMessage(CC.RED + "You're not allowed to disguise on this server.");
      return false;
   };

   public DisguiseManager(CorePlugin plugin) {
      this.plugin = plugin;
   }

   public boolean disguise(Player player, String skinName, String displayName) throws Exception {
      GameProfile targetProfile = (GameProfile)this.skinCache.get(skinName.toLowerCase());
      if (targetProfile == null) {
         UUIDFetcher uuidFetcher = new UUIDFetcher(Collections.singletonList(skinName));
         Map<String, UUID> fetched = uuidFetcher.call();
         Optional<UUID> fetchedUuid = fetched.values().stream().findFirst();
         if (!fetchedUuid.isPresent()) {
            player.sendMessage(CC.RED + "\"" + skinName + "\" is not a real skin.");
            return false;
         }

         targetProfile = this.loadGameProfile((UUID)fetchedUuid.get(), skinName);
      }

      CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).setDisguiseRank(this.getDisguiseData(player.getUniqueId()).disguiseRank());
      if (!this.originalCache.containsKey(player.getUniqueId())) {
         this.originalCache.put(player.getUniqueId(), GameProfileUtil.clone(((CraftPlayer)player).getHandle().getProfile()));
      }

      (new server.pvptemple.disguise.UpdateSkinTask(this.plugin, player, targetProfile, displayName)).runTask(this.plugin);
      return true;
   }

   public void undisguise(Player player) {
      GameProfile originalProfile = (GameProfile)this.originalCache.remove(player.getUniqueId());
      if (originalProfile != null) {
         (new UpdateSkinTask(this.plugin, player, originalProfile, originalProfile.getName())).runTask(this.plugin);
         Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
         if (mineman != null) {
            mineman.setDisguiseRank((Rank)null);
         }
      }

   }

   public GameProfile loadGameProfile(UUID uniqueId, String skinName) {
      GameProfile profile = (GameProfile)this.skinCache.get(skinName.toLowerCase());
      BufferedReader reader = null;

      try {
         if (profile == null || !profile.getProperties().containsKey("textures")) {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId.toString().replace("-", "") + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("User-Agent", "Core");
            connection.setDoOutput(true);
            connection.connect();
            if (connection.getResponseCode() == 200) {
               reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String response = reader.readLine();
               JSONObject object = (JSONObject)JSONValue.parse(response);
               skinName = (String)object.get("name");
               if (profile == null) {
                  profile = new GameProfile(uniqueId, skinName);
               }

               for(Object obj : (JSONArray)object.get("properties")) {
                  JSONObject property = (JSONObject)obj;
                  String propertyName = (String)property.get("name");
                  profile.getProperties().put(propertyName, new Property(propertyName, (String)property.get("value"), (String)property.get("signature")));
               }

               this.skinCache.put(skinName.toLowerCase(), profile);
               MinecraftServer.getServer().getUserCache().a(profile);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (IOException var21) {
            }
         }

      }

      return profile;
   }

   public boolean isDisguised(UUID uuid) {
      return this.originalCache.containsKey(uuid);
   }

   public boolean isDisguised(Player player) {
      return this.isDisguised(player.getUniqueId());
   }

   public DisguiseData getDisguiseData(UUID uuid) {
      DisguiseData data = this.disguiseData.get(uuid);
      if (data == null) {
         data = new DisguiseData();
         this.disguiseData.put(uuid, data);
      }

      return data;
   }

    public class DisguiseData {
      private String displayName;
      private String skinName;
      private Rank disguiseRank;

      public String displayName() {
         return this.displayName;
      }

      public String skinName() {
         return this.skinName;
      }

      public Rank disguiseRank() {
         return this.disguiseRank;
      }

      public DisguiseData displayName(String displayName) {
         this.displayName = displayName;
         return this;
      }

      public DisguiseData skinName(String skinName) {
         this.skinName = skinName;
         return this;
      }

      public DisguiseData disguiseRank(Rank disguiseRank) {
         this.disguiseRank = disguiseRank;
         return this;
      }
   }
}
