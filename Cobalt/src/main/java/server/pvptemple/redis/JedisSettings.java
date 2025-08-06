package server.pvptemple.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisSettings {
   private final String address;
   private final int port;
   private final String password;
   private final JedisPool jedisPool;

   public JedisSettings(String address, int port, String password) {
      this(address, port, password, new JedisPoolConfig());
   }

   public JedisSettings(String address, int port, String password, JedisPoolConfig config) {
      this.address = address;
      this.port = port;
      this.password = password;
      this.jedisPool = new JedisPool(config, this.address, this.port, 0, this.password);
   }

   public JedisSettings(String address, String password) {
      this(address, 6379, password);
   }

   public JedisSettings(String address) {
      this(address, (String)null);
   }

   public boolean hasPassword() {
      return this.password != null;
   }

   public String getAddress() {
      return this.address;
   }

   public int getPort() {
      return this.port;
   }

   public String getPassword() {
      return this.password;
   }

   public JedisPool getJedisPool() {
      return this.jedisPool;
   }
}
