package server.pvptemple.redis;

import java.beans.ConstructorProperties;
import redis.clients.jedis.Jedis;
import server.pvptemple.redis.JedisSettings;

public class JedisImplementation {
   protected final server.pvptemple.redis.JedisSettings jedisSettings;

   protected void cleanup(Jedis jedis) {
      if (jedis != null) {
         jedis.close();
      }

   }

   protected Jedis getJedis() {
      return this.jedisSettings.getJedisPool().getResource();
   }

   @ConstructorProperties({"jedisSettings"})
   public JedisImplementation(JedisSettings jedisSettings) {
      this.jedisSettings = jedisSettings;
   }
}
