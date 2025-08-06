package server.pvptemple.util.ttl;

import server.pvptemple.util.ttl.TtlHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TtlHashMap<K, V> implements Map<K, V>, TtlHandler<K> {
   private final HashMap<K, Long> timestamps = new HashMap();
   private final HashMap<K, V> store = new HashMap();
   private final long ttl;

   public TtlHashMap(TimeUnit ttlUnit, long ttlValue) {
      this.ttl = ttlUnit.toNanos(ttlValue);
   }

   public V get(Object key) {
      V value = (V)this.store.get(key);
      if (value != null && this.expired(key, value)) {
         this.store.remove(key);
         this.timestamps.remove(key);
         return null;
      } else {
         return value;
      }
   }

   private boolean expired(Object key, V value) {
      return System.nanoTime() - (Long)this.timestamps.get(key) > this.ttl;
   }

   public void onExpire(K element) {
   }

   public long getTimestamp(K element) {
      return (Long)this.timestamps.get(element);
   }

   public V put(K key, V value) {
      this.timestamps.put(key, System.nanoTime());
      return (V)this.store.put(key, value);
   }

   public int size() {
      return this.store.size();
   }

   public boolean isEmpty() {
      return this.store.isEmpty();
   }

   public boolean containsKey(Object key) {
      V value = (V)this.store.get(key);
      if (value != null && this.expired(key, value)) {
         this.store.remove(key);
         this.timestamps.remove(key);
         return false;
      } else {
         return this.store.containsKey(key);
      }
   }

   public boolean containsValue(Object value) {
      return this.store.containsValue(value);
   }

   public V remove(Object key) {
      this.timestamps.remove(key);
      return (V)this.store.remove(key);
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      for(Entry<? extends K, ? extends V> e : m.entrySet()) {
         this.put(e.getKey(), e.getValue());
      }

   }

   public void clear() {
      this.timestamps.clear();
      this.store.clear();
   }

   public Set<K> keySet() {
      this.clearExpired();
      return Collections.unmodifiableSet(this.store.keySet());
   }

   public Collection<V> values() {
      this.clearExpired();
      return Collections.unmodifiableCollection(this.store.values());
   }

   public Set<Entry<K, V>> entrySet() {
      this.clearExpired();
      return Collections.unmodifiableSet(this.store.entrySet());
   }

   private void clearExpired() {
      for(K k : this.store.keySet()) {
         this.get(k);
      }

   }
}
