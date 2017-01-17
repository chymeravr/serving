package com.chymeravr.rqhandler.entities.v1.response;

import lombok.Data;

import java.util.Map;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class AdResponse {
    private final int statusCode;
    private final String status;
    private final int experimentId;
    private final Map<String, ResponseObjects.AdMeta> ads;
}
