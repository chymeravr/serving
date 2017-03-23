package com.chymeravr.serving.entities;

import com.chymeravr.schemas.serving.AdMeta;
import com.chymeravr.schemas.serving.ImpressionInfo;
import com.chymeravr.schemas.serving.ResponseCode;
import com.chymeravr.schemas.serving.ServingResponse;
import com.chymeravr.serving.entities.Impression;
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
    private final int status;
    private final List<Integer> experimentId;
    private final Map<String, Impression> ads;

    public ServingResponse getServingResponse(String requestId) {
        return new ServingResponse(responseCode, ads.entrySet().stream().
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createAdMeta(entry.getValue()))
                ), requestId);
    }

    public Map<String, ImpressionInfo> getImpressionLogInfo() {
        return ads.entrySet().stream().
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Impression impression = entry.getValue();
                            return new ImpressionInfo(impression.getServingId(),
                                    impression.getAdgroup().getAdvertiserId(),
                                    impression.getAdgroup().getId(),
                                    impression.getAd().getId(),
                                    impression.getCostPrice(),
                                    impression.getSellingPrice(),
                                    impression.getAd().getUrl(),
                                    impression.getAdgroup().getPricingModel());
                        })
                );
    }

    private AdMeta createAdMeta(Impression impression) {
        AdMeta adMeta = new AdMeta(impression.getServingId(), impression.getAd().getUrl());
        String landingPage = impression.getAd().getLandingPage();
        if (landingPage != null) {
            adMeta.setClickUrl(landingPage);
        }

        String appName = impression.getAdgroup().getAppName();
        if (appName != null) {
            adMeta.setAppName(appName);
        }
        
        return adMeta;
    }
}
