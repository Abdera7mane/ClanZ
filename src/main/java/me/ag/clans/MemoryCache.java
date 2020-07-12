package me.ag.clans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MemoryCache<K, V> extends ConcurrentHashMap<K, V> {
    private Map<K, Long> timeMap = new ConcurrentHashMap<>();
    private long expiryInMillis = 1000;

    public MemoryCache() {
        new CleanerThread().start();
    }

    public MemoryCache(long expiryInMillis) {
        this.expiryInMillis = expiryInMillis;
        new CleanerThread().start();
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, true);
    }

    public V put(K key, V value, boolean expire) {
        if (expire) {
            timeMap.put(key, System.currentTimeMillis());
        } else {
            timeMap.remove(key);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    class CleanerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                cleanup();
                try {
                    Thread.sleep(expiryInMillis / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void cleanup() {
            for (K key : timeMap.keySet()) {
                if (System.currentTimeMillis() > (timeMap.get(key) + expiryInMillis)) {
                    remove(key);
                    timeMap.remove(key);
                }
            }
        }
    }
}
