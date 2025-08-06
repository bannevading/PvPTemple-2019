package server.pvptemple.util.ttl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class TtlArrayList<E> implements List<E>, TtlHandler<E> {
   private final HashMap<E, Long> timestamps = new HashMap();
   private final List<E> store = new ArrayList();
   private final long ttl;

   public TtlArrayList(TimeUnit ttlUnit, long ttlValue) {
      this.ttl = ttlUnit.toNanos(ttlValue);
   }

   private boolean expired(E value) {
      return System.nanoTime() - (Long)this.timestamps.get(value) > this.ttl;
   }

   public long getTimestamp(E value) {
      return (Long)this.timestamps.get(value);
   }

   public void onExpire(E element) {
   }

   public E get(int index) {
      E e = (E)this.store.get(index);
      if (e != null && this.timestamps.containsKey(e) && this.expired(e)) {
         this.store.remove(e);
         this.timestamps.remove(e);
         return null;
      } else {
         return e;
      }
   }

   public int indexOf(Object o) {
      return this.store.indexOf(o);
   }

   public int lastIndexOf(Object o) {
      return this.store.lastIndexOf(o);
   }

   public E set(int index, E e) {
      this.timestamps.put(e, System.nanoTime());
      return (E)this.store.set(index, e);
   }

   public boolean add(E value) {
      this.timestamps.put(value, System.nanoTime());
      return this.store.add(value);
   }

   public void add(int i, E value) {
      this.timestamps.put(value, System.nanoTime());
      this.store.add(i, value);
   }

   public boolean addAll(Collection<? extends E> c) {
      return this.store.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends E> c) {
      return this.store.addAll(index, c);
   }

   public int size() {
      return this.store.size();
   }

   public boolean isEmpty() {
      return this.store.isEmpty();
   }

   public boolean contains(Object value) {
      if (value != null && this.store.contains(value) && this.timestamps.containsKey(value) && this.expired((E) value)) {
         this.store.remove(value);
         this.timestamps.remove(value);
         return false;
      } else {
         return this.store.contains(value);
      }
   }

   public boolean remove(Object value) {
      boolean cont = this.contains(value);
      this.timestamps.remove(value);
      this.store.remove(value);
      return cont;
   }

   public E remove(int i) {
      return (E)this.store.remove(i);
   }

   public boolean removeAll(Collection<?> a) {
      for(Object object : a) {
         this.timestamps.remove(object);
      }

      return this.store.removeAll(a);
   }

   public void clear() {
      this.timestamps.clear();
      this.store.clear();
   }

   public Object[] toArray() {
      return this.store.toArray();
   }

   public Object[] toArray(Object[] a) {
      return this.store.toArray(a);
   }

   public ListIterator<E> listIterator() {
      return this.store.listIterator();
   }

   public ListIterator<E> listIterator(int a) {
      return this.store.listIterator(a);
   }

   public Iterator<E> iterator() {
      return this.store.iterator();
   }

   public boolean retainAll(Collection<?> c) {
      return this.store.retainAll(c);
   }

   public List<E> subList(int fromIndex, int toIndex) {
      return this.store.subList(fromIndex, toIndex);
   }

   public boolean containsAll(Collection<?> c) {
      return this.store.containsAll(c);
   }
}
