package com.whatakitty.framework.cache.utils;

/**
 * Check utils
 *
 * @author WhatAKitty
 * @date 2018/07/08
 * @description
 **/
public class Asserts {

    public static void notNull(Object obj) {
        notNull(obj, null);
    }

    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    private Asserts() {}

}
