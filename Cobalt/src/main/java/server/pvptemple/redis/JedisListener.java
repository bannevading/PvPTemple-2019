package server.pvptemple.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import redis.clients.jedis.Jedis;
import server.pvptemple.redis.JedisSettings;

public abstract class JedisListener {
   private final JedisSettings jedisSettings;
   private final String channel;
   private final Object parameter;

   public JedisListener(JedisSettings jedisSettings, String channel, Object parameter) {
      this.jedisSettings = jedisSettings;
      this.channel = channel;
      if (parameter == null) {
         this.parameter = new JsonObject();
      } else {
         this.parameter = parameter;
      }

      this.listen();
   }

   public JedisListener(JedisSettings jedisSettings, String channel) {
      this(jedisSettings, channel, (Object)null);
   }

   public abstract void respond(String var1, Object var2);

   private void listen() {
      (new Thread(() -> {
         while(true) {
            Jedis jedis = null;

            try {
               jedis = this.jedisSettings.getJedisPool().getResource();

               try {
                  List<String> messages = jedis.blpop(0, (String)this.channel);
                  if (this.parameter instanceof JsonObject) {
                     this.respond((String)messages.get(0), (new JsonParser()).parse((String)messages.get(1)).getAsJsonObject());
                  } else {
                     this.respond((String)messages.get(0), messages.get(1));
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            } finally {
               if (jedis != null) {
                  jedis.close();
               }

            }
         }
      })).start();
   }
}
