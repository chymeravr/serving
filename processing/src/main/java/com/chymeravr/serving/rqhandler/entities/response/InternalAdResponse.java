package com.chymeravr.serving.rqhandler.entities.response;

import com.chymeravr.serving.thrift.ImpressionInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class InternalAdResponse {
    private final int statusCode;
    private final String status;
    private final List<Integer> experimentId;
    private final Map<String, ImpressionInfo> ads;
}
