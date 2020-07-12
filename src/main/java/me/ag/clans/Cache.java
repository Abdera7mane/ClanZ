package me.ag.clans;

public class Cache<K, V> extends MemoryCache<K, V> {
    public Cache(long expiryInMillis) {
        super(expiryInMillis);
    }

    @Override
    public V remove(Object key){
        V removed = super.remove(key);
        System.out.println("removed: " + removed);
        return removed;
    }

}
