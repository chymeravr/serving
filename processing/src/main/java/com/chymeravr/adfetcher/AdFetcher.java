package com.chymeravr.adfetcher;

import com.chymeravr.ad.AdCache;
import com.chymeravr.ad.AdEntity;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.adgroup.AdgroupEntity;
import com.chymeravr.rqhandler.entities.request.Request;
import com.chymeravr.rqhandler.entities.request.RequestObjects;
import com.chymeravr.rqhandler.entities.response.Response;
import com.chymeravr.rqhandler.entities.response.ResponseObjects;
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

    public Response getAdResponse(Request adRequest, List<Integer> expIds) {
        List<RequestObjects.Placement> placements = adRequest.getPlacements();
        int hmdId = adRequest.getHmdId();
        ArrayList<AdgroupEntity> adgroupsForHmd = new ArrayList<>(adgroupCache.getAdgroupsForHmd(hmdId));

        adgroupsForHmd.sort(Comparator.comparingDouble(x -> -x.getBid())); // reverse sort

        List<ResponseObjects.AdMeta> topAds = getTopAds(adgroupsForHmd, placements.size());
        // Assign ads to a placement
        Map<String, ResponseObjects.AdMeta> adsMap = new HashMap<>();
        for (int i = 0; i < topAds.size(); i++) { // At most as many top Ads as placements
            adsMap.put(placements.get(i).getId(), topAds.get(i));
        }
        return new Response(200, "OK", expIds, adsMap);
    }

    private List<ResponseObjects.AdMeta> getTopAds(ArrayList<AdgroupEntity> adgroupsForHmd, int adsToSelect) {
        List<ResponseObjects.AdMeta> ads = new ArrayList<>();
        int adsSelected = 0;

        for (AdgroupEntity adgroupEntity : adgroupsForHmd) {
            Set<AdEntity> adsForAdgroup = adCache.getAdsForAdgroup(adgroupEntity.getId());
            for (AdEntity ad : adsForAdgroup) {
                if (adsSelected == adsToSelect) {
                    return ads;
                }
                ads.add(new ResponseObjects.AdMeta(UUID.randomUUID().toString(), CREATIVE_URL_PREFIX + ad.getUrl()));
                adsSelected++;
            }
        }
        return ads;
    }

}
