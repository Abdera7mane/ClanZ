package me.ag.clans;

import org.jetbrains.annotations.NotNull;

public class Cache<K, V> extends MemoryCache<K, V> {
    public Cache(long expiryInMillis) {
        super(expiryInMillis);
    }

    @Override
    public V remove(@NotNull Object key) {
        V removed = super.remove(key);
        System.out.println("removed: " + removed);
        return removed;
    }
}