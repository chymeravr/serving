package com.chymeravr.serving.processing.logger;

import com.chymeravr.serving.logging.EventLogger;
import lombok.Data;

/**
 * Created by rubbal on 19/1/17.
 */
@Data
public class ResponseLogger {
    private final EventLogger eventLogger;

    public void sendMessage(String key, String event) {
        eventLogger.sendMessage(key, event);
    }
}
