package com.whatakitty.framework.cache;

import com.whatakitty.framework.cache.exception.CacheLoaderFailedException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * cache interface
 *
 * @author WhatAKitty
 * @date 2018/07/05
 * @description
 **/
public interface Cache<K, V> {

    /**
     * put value into cache with named key
     *
     * @param key   cache item name
     * @param value cache value
     */
    default void put(K key, V value) {
        put(key, value, 0L);
    }

    /**
     * put value into cache with named key and expired after some time.
     *
     * @param key     cache item name
     * @param value   cache value
     * @param timeout the time that cache item will be expired
     */
    void put(K key, V value, long timeout);

    /**
     * get cache value
     *
     * @param key cache item name
     * @return the cache value, return null if not exists
     * @throws InterruptedException interrupted exception
     */
    default Optional<V> get(K key) throws InterruptedException {
        try {
            return get(key, new CacheLoader.EmptyCacheLoader<>());
        } catch (CacheLoaderFailedException e) {
            // it will never happen, ignore
            return Optional.empty();
        }
    }

    /**
     * get cache value
     *
     * priority: cache map -> loader
     *
     * @param key    cache item name
     * @param loader the cache will be loaded from loader if not exists in cache map
     * @return the cache value, return null if not exists
     * @throws InterruptedException       interrupted exception
     * @throws CacheLoaderFailedException loader failed to get origin value
     */
    Optional<V> get(K key, CacheLoader<V> loader) throws InterruptedException, CacheLoaderFailedException;

    /**
     * Remove certain cache item
     *
     * @param key certain cache key
     * @return return true if remove success or false remove failed.
     */
    boolean remove(K key);

    /**
     * Clear all cache
     *
     * @return return true if clear success or false clear failed.
     */
    boolean clearAll();

    /**
     * Get the cache size
     *
     * @return cache size
     */
    int size();

    /**
     * the cached keys
     *
     * @return keys set
     */
    Set<K> keys();

    /**
     * if the cache contains the certain cache item
     *
     * @param key the key that need to checked
     * @return true if the cache contains or false
     */
    boolean contains(K key);

    /**
     * Snapshot of this cache
     *
     * @return
     */
    Map<K, CacheObject<K, V>> snapshot();

}
