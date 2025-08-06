package server.pvptemple.redis;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import server.pvptemple.redis.JedisSettings;
import server.pvptemple.redis.subscription.JedisSubscriptionGenerator;
import server.pvptemple.redis.subscription.JedisSubscriptionHandler;
import server.pvptemple.redis.subscription.impl.JsonJedisSubscriptionGenerator;
import server.pvptemple.redis.subscription.impl.StringJedisSubscriptionGenerator;

public class JedisSubscriber<K> {
   private static final Map<Class, JedisSubscriptionGenerator> GENERATORS = new HashMap();
   protected final String channel;
   private final Class<K> typeParameter;
   private final server.pvptemple.redis.JedisSettings jedisSettings;
   private final Jedis jedis;
   private JedisPubSub pubSub;
   private JedisSubscriptionHandler<K> jedisSubscriptionHandler;

   public JedisSubscriber(JedisSettings jedisSettings, String channel, final Class<K> typeParameter, JedisSubscriptionHandler<K> jedisSubscriptionHandler) {
      this.jedisSettings = jedisSettings;
      this.channel = channel;
      this.typeParameter = typeParameter;
      this.jedisSubscriptionHandler = jedisSubscriptionHandler;
      this.pubSub = new JedisPubSub() {
         public void onMessage(String channel, String message) {
            JedisSubscriptionGenerator<K> jedisSubscriptionGenerator = (JedisSubscriptionGenerator)JedisSubscriber.GENERATORS.get(typeParameter);
            if (jedisSubscriptionGenerator != null) {
               K object = jedisSubscriptionGenerator.generateSubscription(message);
               JedisSubscriber.this.jedisSubscriptionHandler.handleMessage(object);
            } else {
               System.out.println("Generator type is null");
            }

         }
      };
      this.jedis = new Jedis(this.jedisSettings.getAddress(), this.jedisSettings.getPort());
      this.authenticate();
      this.connect();
   }

   private void authenticate() {
      if (this.jedisSettings.hasPassword()) {
         this.jedis.auth(this.jedisSettings.getPassword());
      }

   }

   private void connect() {
      Logger.getGlobal().info("Jedis is now reading on " + this.channel);
      (new Thread(() -> {
         try {
            this.jedis.subscribe(this.pubSub, this.channel);
         } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().info("For some odd reason our JedisSubscriber(" + this.channel + ") threw an exception");
            this.close();
            this.connect();
         }

      })).start();
   }

   public void close() {
      Logger.getGlobal().info("Jedis is no longer reading on " + this.channel);
      if (this.pubSub != null) {
         this.pubSub.unsubscribe();
      }

      if (this.jedis != null) {
         this.jedis.close();
      }

   }

   public JedisPubSub getPubSub() {
      return this.pubSub;
   }

   static {
      GENERATORS.put(String.class, new StringJedisSubscriptionGenerator());
      GENERATORS.put(JsonObject.class, new JsonJedisSubscriptionGenerator());
   }
}
