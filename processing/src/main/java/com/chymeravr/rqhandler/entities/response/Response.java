package com.chymeravr.rqhandler.entities.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class Response {
    private final int statusCode;
    private final String status;
    private final List<Integer> experimentId;
    private final Map<String, ResponseObjects.AdMeta> ads;
}
