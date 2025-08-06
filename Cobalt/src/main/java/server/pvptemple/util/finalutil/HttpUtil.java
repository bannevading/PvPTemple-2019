package server.pvptemple.util.finalutil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class HttpUtil {
   public static String getHastebin(String body) {
      try {
         URL url = new URL("https://www.hastebin.com/documents");
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("POST");
         connection.setRequestProperty("User-Agent", "Mozilla/5.0");
         connection.setDoInput(true);
         connection.setDoOutput(true);
         DataOutputStream os = new DataOutputStream(connection.getOutputStream());
         os.writeBytes(body);
         os.flush();
         os.close();
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         JSONParser parser = new JSONParser();
         JSONObject object = (JSONObject)parser.parse(reader.readLine());
         return object.get("key").toString();
      } catch (IOException | ParseException e) {
         ((Exception)e).printStackTrace();
         return null;
      }
   }
}
