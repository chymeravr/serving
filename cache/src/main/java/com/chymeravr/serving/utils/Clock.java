package com.chymeravr.serving.utils;

/**
 * Created by rubbal on 16/1/17.
 */
public interface Clock {
    default public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
