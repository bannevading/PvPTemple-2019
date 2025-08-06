package server.pvptemple.redis.subscription;

public interface JedisSubscriptionHandler<K> {
   void handleMessage(K var1);
}
