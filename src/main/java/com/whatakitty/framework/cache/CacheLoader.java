package com.whatakitty.framework.cache;

/**
 * The loader of cache.
 *
 * @author WhatAKitty
 * @date 2018/07/05
 * @description
 **/
@FunctionalInterface
public interface CacheLoader<V> {

    public static class EmptyCacheLoader<V> implements CacheLoader<V> {

        @Override
        public V call() {
            return null;
        }
    }

    /**
     * Get data
     *
     * @return The data from datasource
     */
    V call();

}
