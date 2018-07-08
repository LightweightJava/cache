package com.whatakitty.framework.cache;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * The object to store cache item info, such as ttl
 *
 * @author WhatAKitty
 * @date 2018/07/05
 * @description
 **/
public class CacheObject<K, V> {

    /**
     * create simple key value cache
     *
     * @param key    cache key
     * @param result cache value
     * @param ttl    the cache when should be expired
     * @param <K>    key type
     * @param <V>    value type
     * @return cache object
     */
    public static <K, V> CacheObject<K, V> createValueCache(K key, V result, long ttl) {
        return new CacheObject<>(key, result, ttl);
    }

    /**
     * create key with future value cache
     *
     * @param key          cache key
     * @param futureResult cache future value function
     * @param ttl          the cache when should be expired
     * @param <K>          key type
     * @param <V>          value type
     * @return cache object
     */
    public static <K, V> CacheObject<K, V> createFutureCache(K key, FutureTask<V> futureResult, long ttl) {
        return new CacheObject<>(key, futureResult, ttl);
    }

    private final K key;
    private final FutureTask<V> futureResult;
    private final V result;
    private long ttl;
    private long accessCount;
    private long lastAccess;

    private CacheObject(K key, FutureTask<V> futureResult, long ttl) {
        this.key = key;
        this.futureResult = futureResult;
        this.result = null;
        this.ttl = ttl;
        this.lastAccess = System.currentTimeMillis();
        this.accessCount = 0;
    }

    private CacheObject(K key, V result, long ttl) {
        this.key = key;
        this.futureResult = null;
        this.result = result;
        this.ttl = ttl;
        this.lastAccess = System.currentTimeMillis();
        this.accessCount = 0;
    }

    /**
     * If the cache object is expired
     *
     * @return return true if item expired or false
     */
    boolean isExpired() {
        if (ttl == 0) {
            return false;
        }
        return lastAccess + ttl < System.currentTimeMillis();
    }

    /**
     * Get cache object with adding the access count and updating the last access time at the same time.
     *
     * @return the cache item value
     * @throws ExecutionException   execution exception
     * @throws InterruptedException interrupted exception
     */
    Optional<V> getObject() throws ExecutionException, InterruptedException {
        lastAccess = System.currentTimeMillis();
        accessCount++;
        if (futureResult == null) {
            return Optional.ofNullable(result);
        }
        if (!futureResult.isDone()) {
            futureResult.run();
        }
        return Optional.ofNullable(futureResult.get());
    }

}
