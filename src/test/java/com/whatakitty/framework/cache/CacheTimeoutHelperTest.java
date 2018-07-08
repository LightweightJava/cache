package com.whatakitty.framework.cache;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author WhatAKitty
 * @date 2018/07/09
 * @description
 **/
public class CacheTimeoutHelperTest {

    @Test
    public void start() {
        MemoryCache<String, String> testCache = new MemoryCache<>(1);
        testCache.put("name", "kitty", 1000);

        try {
            Optional<String> value = testCache.get("name");
            Assert.assertTrue(value.isPresent());
            Assert.assertEquals("kitty", value.get());

            Thread.sleep(8 * 1000);

            Optional<String> another = testCache.get("name");
            Assert.assertFalse(another.isPresent());
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

}