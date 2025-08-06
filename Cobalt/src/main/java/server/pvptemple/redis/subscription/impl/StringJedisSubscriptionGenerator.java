package server.pvptemple.redis.subscription.impl;

import server.pvptemple.redis.subscription.JedisSubscriptionGenerator;

public class StringJedisSubscriptionGenerator implements JedisSubscriptionGenerator<String> {
   public String generateSubscription(String message) {
      return message;
   }
}
