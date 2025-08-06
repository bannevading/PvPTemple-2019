package server.pvptemple.redis.subscription;

public interface JedisSubscriptionGenerator<K> {
   K generateSubscription(String var1);
}
