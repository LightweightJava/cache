package com.whatakitty.framework.cache.exception;

/**
 * cache loader execution failed and should throw an exception
 *
 * @author WhatAKitty
 * @date 2018/07/08
 * @description
 **/
public class CacheLoaderFailedException extends Exception {

    public CacheLoaderFailedException() {
        super();
    }

    public CacheLoaderFailedException(String message) {
        super(message);
    }

    public CacheLoaderFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheLoaderFailedException(Throwable cause) {
        super(cause);
    }

    protected CacheLoaderFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
