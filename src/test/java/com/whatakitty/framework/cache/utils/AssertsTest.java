package com.whatakitty.framework.cache.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Asserts unit test
 *
 * @author WhatAKitty
 * @date 2018/07/09
 * @description
 **/
public class AssertsTest {

    @Test
    public void notNull() {
        try {
            Asserts.notNull(null, "test null");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
            Assert.assertEquals("test null", e.getMessage());
        }
    }

    @Test
    public void isTrue() {
        try {
            Asserts.isTrue(false, "test true");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
            Assert.assertEquals("test true", e.getMessage());
        }
    }
}