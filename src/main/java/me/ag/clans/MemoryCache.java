package me.ag.clans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

public abstract class MemoryCache<K, V> extends ConcurrentHashMap<K, V> {
    private final Map<K, Long> timeMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private long expireInMillis = 1000L;

    public MemoryCache() {
        scheduler.scheduleAtFixedRate(new CleanerThread(), 0, expireInMillis, TimeUnit.MILLISECONDS);
    }

    public MemoryCache(long expiryInMillis) {
        this.expireInMillis = expiryInMillis;
        scheduler.scheduleAtFixedRate(new CleanerThread(), 0, expiryInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public V put(@NotNull K key, @NotNull V value) {
        return this.put(key, value, true);
    }

    public V put(@NotNull K key, @NotNull V value, boolean expire) {
        if (expire) {
            this.timeMap.put(key, System.currentTimeMillis());
        } else {
            this.timeMap.remove(key);
        }

        return super.put(key, value);
    }

    @Override
    public V remove(@NotNull Object key) {
        this.timeMap.remove(key);
        return super.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {

        for (K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    class CleanerThread extends Thread {

        @Override
        public void run() {
            this.cleanup();
        }

        private void cleanup() {
            for (K key : timeMap.keySet())  {
                if (System.currentTimeMillis() > timeMap.get(key) + expireInMillis) {
                    MemoryCache.this.remove(key);
                }
            }
        }
    }
}