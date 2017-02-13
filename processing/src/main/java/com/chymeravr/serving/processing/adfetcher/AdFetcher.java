package com.chymeravr.serving.processing.adfetcher;

import com.chymeravr.schemas.serving.ImpressionInfo;
import com.chymeravr.schemas.serving.Placement;
import com.chymeravr.schemas.serving.ResponseCode;
import com.chymeravr.schemas.serving.ServingRequest;
import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.cache.ad.AdEntity;
import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.cache.adgroup.AdgroupEntity;
import com.chymeravr.serving.processing.rqhandler.entities.response.InternalAdResponse;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rubbal on 19/1/17.
 */
@Data
public class AdFetcher {
    private final AdgroupCache adgroupCache;
    private final AdCache adCache;
    private static final String CREATIVE_URL_PREFIX = "https://chymerastatic.blob.core.windows.net/creatives/";

    public InternalAdResponse getAdResponse(ServingRequest adRequest, List<Integer> expIds) {
        List<Placement> placements = adRequest.getPlacements();
        int hmdId = adRequest.getHmdId();
        ArrayList<AdgroupEntity> adgroupsForHmd = new ArrayList<>(adgroupCache.getAdgroupsForHmd(hmdId));

        List<AdgroupEntity> adgroupsWithBudget = adgroupsForHmd.stream().filter(x ->
                x.getTodayBurn() < x.getDailyBudget() &&
                        x.getTotalBurn() < x.getTotalBudget() &&
                        x.getCmpTodayBurn() < x.getCmpDailyBudget() &&
                        x.getCmpTotalBurn() < x.getCmpTotalBudget()

        ).collect(Collectors.toList());

        adgroupsWithBudget.sort(Comparator.comparingDouble(x -> -x.getBid())); // reverse sort

        List<ImpressionInfo> topAds = getTopAds(adgroupsWithBudget, placements.size());
        // Assign ads to a placement
        Map<String, ImpressionInfo> adsMap = new HashMap<>();
        for (int i = 0; i < topAds.size(); i++) { // At most as many top Ads as placements
            adsMap.put(placements.get(i).getId(), topAds.get(i));
        }
        return new InternalAdResponse(adsMap.size() > 0 ? ResponseCode.SERVED : ResponseCode.NO_AD, "OK", expIds, adsMap);
    }

    private List<ImpressionInfo> getTopAds(List<AdgroupEntity> adgroupsForHmd, int adsToSelect) {
        List<ImpressionInfo> ads = new ArrayList<>();
        int adsSelected = 0;

        for (AdgroupEntity adgroupEntity : adgroupsForHmd) {
            Set<AdEntity> adsForAdgroup = adCache.getAdsForAdgroup(adgroupEntity.getId());
            for (AdEntity ad : adsForAdgroup) {
                if (adsSelected == adsToSelect) {
                    return ads;
                }
                ImpressionInfo impressionInfo = new ImpressionInfo(UUID.randomUUID().toString(),
                        adgroupEntity.getAdvertiserId(),
                        adgroupEntity.getId(),
                        ad.getId(),
                        adgroupEntity.getBid(),
                        adgroupEntity.getBid() * 0.6,
                        CREATIVE_URL_PREFIX + ad.getUrl(),
                        adgroupEntity.getPricingModel());
                ads.add(impressionInfo);
                adsSelected++;
            }
        }
        return ads;
    }

}
