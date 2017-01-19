package com.chymeravr.rqhandler.entities.response;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by rubbal on 19/1/17.
 */
@Data
public class AdResponse {
    private final int statusCode;
    private final String status;
    private final List<Integer> experimentId;
    private final Map<String, ResponseObjects.AdMeta> ads;

    public AdResponse(InternalAdResponse internalAdResponse) {
        this.statusCode = internalAdResponse.getStatusCode();
        this.status = internalAdResponse.getStatus();
        this.experimentId = internalAdResponse.getExperimentId();
        this.ads = internalAdResponse.getAds().entrySet().stream().
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> new ResponseObjects.AdMeta(v.getValue().getServingId(), v.getValue().getCreativeUrl()))
                );
    }

}
