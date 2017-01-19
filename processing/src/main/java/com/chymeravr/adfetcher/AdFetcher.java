package com.chymeravr.adfetcher;

import com.chymeravr.ad.AdCache;
import com.chymeravr.ad.AdEntity;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.adgroup.AdgroupEntity;
import com.chymeravr.rqhandler.entities.request.Request;
import com.chymeravr.rqhandler.entities.response.InternalAdResponse;
import com.chymeravr.thrift.serving.ImpressionInfo;
import com.chymeravr.thrift.serving.Placement;
import lombok.Data;

import java.util.*;

/**
 * Created by rubbal on 19/1/17.
 */
@Data
public class AdFetcher {
    private final AdgroupCache adgroupCache;
    private final AdCache adCache;
    private static final String CREATIVE_URL_PREFIX = "https://chymcreative.blob.core.windows.net/creatives/";

    public InternalAdResponse getAdResponse(Request adRequest, List<Integer> expIds) {
        List<Placement> placements = adRequest.getPlacements();
        int hmdId = adRequest.getHmdId();
        ArrayList<AdgroupEntity> adgroupsForHmd = new ArrayList<>(adgroupCache.getAdgroupsForHmd(hmdId));

        adgroupsForHmd.sort(Comparator.comparingDouble(x -> -x.getBid())); // reverse sort

        List<ImpressionInfo> topAds = getTopAds(adgroupsForHmd, placements.size());
        // Assign ads to a placement
        Map<String, ImpressionInfo> adsMap = new HashMap<>();
        for (int i = 0; i < topAds.size(); i++) { // At most as many top Ads as placements
            adsMap.put(placements.get(i).getId(), topAds.get(i));
        }
        return new InternalAdResponse(200, "OK", expIds, adsMap);
    }

    private List<ImpressionInfo> getTopAds(ArrayList<AdgroupEntity> adgroupsForHmd, int adsToSelect) {
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
                        CREATIVE_URL_PREFIX + ad.getUrl());
                ads.add(impressionInfo);
                adsSelected++;
            }
        }
        return ads;
    }

}
