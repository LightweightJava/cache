package com.whatakitty.framework.cache;

import com.rits.cloning.Cloner;
import com.whatakitty.framework.cache.exception.CacheLoaderFailedException;
import com.whatakitty.framework.cache.utils.Asserts;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.StampedLock;

/**
 * Memory cache
 *
 * @author WhatAKitty
 * @date 2018/07/05
 * @description
 **/
public class MemoryCache<K, V> implements Cache<K, V> {

    private final Map<K, CacheObject<K, V>> cacheMap;
    private final StampedLock stampedLock = new StampedLock();
    private final CacheTimeoutHelper<K, V> cacheTimeoutHelper;

    public MemoryCache(int cap) {
        cacheMap = new HashMap<>(cap);
        cacheTimeoutHelper = new CacheTimeoutHelper<>(this, 5);
    }

    @Override
    public void put(K key, V value, long timeout) {
        long stamp = stampedLock.writeLock();

        try {
            // do clean task if clean task is not started
            if (!cacheTimeoutHelper.isStarted()) {
                cacheTimeoutHelper.start();
            }

            CacheObject<K, V> cacheObject = CacheObject.createValueCache(key, value, timeout);
            cacheMap.put(key, cacheObject);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    @Override
    public Optional<V> get(K key, final CacheLoader<V> loader) throws InterruptedException, CacheLoaderFailedException {
        Asserts.notNull(loader);
        long stamp = stampedLock.readLock();

        try {
            CacheObject<K, V> obj = cacheMap.get(key);
            if (obj == null && loader instanceof CacheLoader.EmptyCacheLoader) {
                return Optional.empty();
            }

            // obj is null or is expired, should load data from loader
            if (obj == null || obj.isExpired()) {
                stamp = stampedLock.tryConvertToWriteLock(stamp);

                if (stamp == 0L) {
                    // lock upgrade failed
                    stamp = stampedLock.writeLock();
                }

                FutureTask<V> futureTask = new FutureTask<>(loader::call);
                obj = CacheObject.createFutureCache(key, futureTask, 0L);
                cacheMap.replace(key, obj);
            }

            return obj.getObject();
        } catch (ExecutionException e) {
            throw new CacheLoaderFailedException(e);
        } finally {
            stampedLock.unlock(stamp);
        }
    }

    @Override
    public boolean remove(K key) {
        long stamp = stampedLock.writeLock();

        try {
            cacheMap.remove(key);
            return true;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean clearAll() {
        long stamp = stampedLock.writeLock();

        try {
            cacheMap.clear();
            return true;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    @Override
    public Set<K> keys() {
        long stamp = stampedLock.readLock();

        try {
            return cacheMap.keySet();
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }

    @Override
    public boolean contains(K key) {
        long stamp = stampedLock.readLock();

        try {
            return cacheMap.containsKey(key);
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }

    @Override
    public Map<K, CacheObject<K, V>> snapshot() {
        long stamp = stampedLock.readLock();

        try {

            final Cloner cloner = new Cloner();
            final Map<K, CacheObject<K, V>> cloned = new HashMap<>(cacheMap.size());

            for (Map.Entry<K, CacheObject<K, V>> entry : cacheMap.entrySet()) {
                CacheObject<K, V> value = entry.getValue();
                CacheObject<K, V> clonedValue = cloner.deepClone(value);
                cloned.put(entry.getKey(), clonedValue);
            }

            return cloned;

        } finally {
            stampedLock.unlockRead(stamp);
        }
    }

}
