package com.whatakitty.framework.cache;

import com.whatakitty.framework.cache.exception.CacheLoaderFailedException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/**
 * memory cache test unit
 *
 * @author WhatAKitty
 * @date 2018/07/08
 * @description
 **/
public class MemoryCacheTest {

    @Test
    public void put() {
        MemoryCache<String, String> testCache = new MemoryCache<>(1);
        testCache.put("name", "kitty");

        try {
            Optional<String> result = testCache.get("name");
            Assert.assertTrue(result.isPresent());
            Assert.assertEquals("kitty", result.get());
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

    @Test
    public void get() {
        MemoryCache<String, String> testCache = new MemoryCache<>(1);
        try {
            Optional<String> result = testCache.get("name", () -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return "Hello";
            });

            Assert.assertTrue(result.isPresent());
            Assert.assertEquals("Hello", result.get());

            long start = System.currentTimeMillis();
            result.get();
            Assert.assertTrue(System.currentTimeMillis() - start < 2 * 1000);
        } catch (InterruptedException | CacheLoaderFailedException e) {
            Assert.fail();
        }
    }

    @Test
    public void remove() {
        MemoryCache<String, String> testCache = new MemoryCache<>(1);
        testCache.put("name", "kitty");
        testCache.put("sex", "body");

        Assert.assertEquals(2, testCache.size());

        testCache.remove("sex");
        try {
            Assert.assertEquals(1, testCache.size());
            Assert.assertFalse(testCache.get("sex").isPresent());
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

    @Test
    public void clearAll() {
        MemoryCache<String, String> testCache = new MemoryCache<>(1);
        testCache.put("name", "kitty");
        testCache.put("sex", "body");

        Assert.assertEquals(2, testCache.size());

        testCache.clearAll();
        try {
            Assert.assertEquals(0, testCache.size());
            Assert.assertFalse(testCache.get("sex").isPresent());
            Assert.assertFalse(testCache.get("name").isPresent());
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

}