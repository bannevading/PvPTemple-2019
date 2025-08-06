package server.pvptemple.redis;

import org.bukkit.plugin.java.JavaPlugin;
import server.pvptemple.redis.JedisSettings;
import server.pvptemple.util.Config;

public class JedisConfig extends Config {
   public JedisConfig(JavaPlugin plugin) {
      super("jedis", plugin);
      if (this.wasCreated) {
         this.getConfig().set("jedis.host", "localhost");
         this.getConfig().set("jedis.port", 6379);
         this.getConfig().set("jedis.password", "asdf");
         this.save();
      }

   }

   public JedisSettings toJedisSettings() {
      return new JedisSettings(this.getConfig().getString("jedis.host"), this.getConfig().getInt("jedis.port"), this.getConfig().getString("jedis.password"));
   }
}
