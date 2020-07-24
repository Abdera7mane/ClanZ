package me.ag.clans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

public abstract class MemoryCache<K, V> extends ConcurrentHashMap<K, V> {
    private final Map<K, Long> timeMap = new ConcurrentHashMap<>();
    private long expiryInMillis = 1000L;

    public MemoryCache() {
        (new CleanerThread()).start();
    }

    public MemoryCache(long expiryInMillis) {
        this.expiryInMillis = expiryInMillis;
        new CleanerThread().start();
    }

    @Override
    public V put(@NotNull K key, @NotNull V value) {
        return this.put(key, value, true);
    }

    public V put(@NotNull K key, @NotNull V value, boolean expire) {
        if (expire) {
            this.timeMap.put(key, System.currentTimeMillis());
        } else if (this.timeMap.get(key) != null) {
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
        CleanerThread() {
        }

        @Override
        public void run() {
            while(true) {
                this.cleanup();

                try {
                    Thread.sleep(expiryInMillis / 2L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void cleanup() {
            for (K key : timeMap.keySet())  {
                if (System.currentTimeMillis() > timeMap.get(key) + expiryInMillis) {
                    MemoryCache.this.remove(key);
                }
            }

        }
    }
}