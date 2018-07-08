package com.whatakitty.framework.cache;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * help cache to be expired
 *
 * @author WhatAKitty
 * @date 2018/07/08
 * @description
 **/
final class CacheTimeoutHelper<K, V> {

    private final Cache<K, V> cache;
    private final long delay;
    private final ScheduledExecutorService executor;
    private volatile boolean started = false;

    /**
     * cache timeout helper
     *
     * @param cache the cache instance
     * @param delay the delay time that execute the clean task, time unit is seconds.
     */
    CacheTimeoutHelper(Cache<K, V> cache, long delay) {
        this.cache = cache;
        this.delay = delay;
        this.executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void start() {
        started = true;
        executor.schedule(() -> {
            // only need to iterator the cache, it will auto remove the expired value
            for (K key : cache.keys()) {
                try {
                    Optional<V> value = cache.get(key);
                    // if value is not found, remove it even the key is not exists
                    if (!value.isPresent()) {
                        cache.remove(key);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    /**
     * the clean task for this cache is started ?
     *
     * @return true if the task is started.
     */
    public boolean isStarted() {
        return started;
    }

}
