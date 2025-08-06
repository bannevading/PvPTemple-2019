package server.pvptemple.redis;

import java.beans.ConstructorProperties;
import redis.clients.jedis.Jedis;
import server.pvptemple.redis.JedisSettings;

public class JedisPublisher<K> {
   private final server.pvptemple.redis.JedisSettings jedisSettings;
   private final String channel;

   public void write(K message) {
      Jedis jedis = null;

      try {
         jedis = this.jedisSettings.getJedisPool().getResource();
         jedis.publish(this.channel, message.toString());
      } finally {
         if (jedis != null) {
            jedis.close();
         }

      }

   }

   @ConstructorProperties({"jedisSettings", "channel"})
   public JedisPublisher(server.pvptemple.redis.JedisSettings jedisSettings, String channel) {
      this.jedisSettings = jedisSettings;
      this.channel = channel;
   }

   public JedisSettings getJedisSettings() {
      return this.jedisSettings;
   }
}
