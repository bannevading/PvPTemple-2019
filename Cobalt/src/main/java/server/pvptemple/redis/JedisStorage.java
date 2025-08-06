package server.pvptemple.redis;

import redis.clients.jedis.Jedis;
import server.pvptemple.redis.JedisImplementation;
import server.pvptemple.redis.JedisSettings;

public class JedisStorage extends JedisImplementation {
   public JedisStorage(JedisSettings jedisSettings) {
      super(jedisSettings);
   }

   public String get(String channel, String key) {
      Jedis jedis = this.getJedis();

      String var4;
      try {
         var4 = (String)jedis.hgetAll(channel).get(key);
      } finally {
         this.cleanup(jedis);
      }

      return var4;
   }

   public void remove(String channel, String key) {
      Jedis jedis = this.getJedis();

      try {
         jedis.hdel(channel, key);
      } finally {
         this.cleanup(jedis);
      }

   }

   public void set(String channel, String key, String value) {
      Jedis jedis = this.getJedis();

      try {
         jedis.hset(channel, key, value);
      } finally {
         this.cleanup(jedis);
      }

   }
}
