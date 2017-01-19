package com.chymeravr.rqhandler.entities.response;

import lombok.Data;

/**
 * Created by rubbal on 17/1/17.
 */
public class ResponseObjects {
    @Data
    public static class AdMeta {
        private final String servingId;
        private final String mediaUrl;
    }
}
