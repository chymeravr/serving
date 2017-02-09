package com.chymeravr.serving.logging;

/**
 * Created by rubbal on 19/1/17.
 */
public interface ResponseLogger {
    void sendMessage(String key, String event, String topic);
}
