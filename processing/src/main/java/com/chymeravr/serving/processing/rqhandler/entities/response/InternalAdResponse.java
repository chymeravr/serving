package com.chymeravr.serving.processing.rqhandler.entities.response;

import com.chymeravr.schemas.serving.AdMeta;
import com.chymeravr.schemas.serving.ImpressionInfo;
import com.chymeravr.schemas.serving.ResponseCode;
import com.chymeravr.schemas.serving.ServingResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class InternalAdResponse {
    private final ResponseCode responseCode;
    private final String status;
    private final List<Integer> experimentId;
    private final Map<String, ImpressionInfo> ads;

    public ServingResponse getServingResponse() {
        return new ServingResponse(responseCode, ads.entrySet().stream().
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> new AdMeta(v.getValue().getServingId(), v.getValue().getCreativeUrl()))
                ));
    }
}
