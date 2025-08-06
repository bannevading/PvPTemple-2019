package server.pvptemple.util.finalutil;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import server.pvptemple.CorePlugin;

public final class UUIDUtil {
   private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
   private static final Map<String, UUID> CACHE = new HashMap();
   private static final JSONParser PARSER = new JSONParser();

   private UUIDUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   private static void writeBody(HttpURLConnection connection, String body) throws Exception {
      OutputStream stream = connection.getOutputStream();
      stream.write(body.getBytes());
      stream.flush();
      stream.close();
   }

   private static HttpURLConnection createConnection() throws Exception {
      URL url = new URL("https://api.mojang.com/profiles/minecraft");
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      return connection;
   }

   private static UUID getUUID(String id) {
      return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
   }

   public static UUID getUUIDFromName(String nig) {
      return CACHE.containsKey(nig) ? (UUID)CACHE.get(nig) : (new AsyncUUIDFetcher(nig)).getUniqueID();
   }

   private static class AsyncUUIDFetcher {
      private UUID uniqueID;

      private AsyncUUIDFetcher(final String nig) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  HttpURLConnection connection = UUIDUtil.createConnection();
                  String body = JSONArray.toJSONString(Collections.singletonList(nig));
                  UUIDUtil.writeBody(connection, body);

                  for(Object profile : (JSONArray)UUIDUtil.PARSER.parse(new InputStreamReader(connection.getInputStream()))) {
                     JSONObject jsonProfile = (JSONObject)profile;
                     String id = (String)jsonProfile.get("id");
                     AsyncUUIDFetcher.this.uniqueID = UUIDUtil.getUUID(id);
                     UUIDUtil.CACHE.put(nig, AsyncUUIDFetcher.this.uniqueID);
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }

            }
         }).runTaskAsynchronously(CorePlugin.getInstance());
      }

      public UUID getUniqueID() {
         return this.uniqueID;
      }
   }
}
