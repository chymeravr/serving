package com.chymeravr.serving.logging;

/**
 * Created by rubbal on 13/3/17.
 * Used for development environments
 */

public class NoOpLogger implements ResponseLogger {

    @Override
    public void sendMessage(String key, String event, String topic) {
        // No-Op
    }
}
