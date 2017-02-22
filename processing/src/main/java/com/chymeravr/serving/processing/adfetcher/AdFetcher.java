package com.chymeravr.serving.processing.adfetcher;

import com.chymeravr.schemas.serving.*;
import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.cache.placement.PlacementCache;
import com.chymeravr.serving.entities.AdEntity;
import com.chymeravr.serving.entities.AdgroupEntity;
import com.chymeravr.serving.entities.Impression;
import com.chymeravr.serving.entities.PlacementEntity;
import com.chymeravr.serving.processing.rqhandler.entities.response.InternalAdResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rubbal on 19/1/17.
 */
@Data
@Slf4j
public class AdFetcher {
    private final AdgroupCache adgroupCache;
    private final AdCache adCache;
    private final PlacementCache placementCache;
    private final double defaultCtr;

    public InternalAdResponse getAdResponse(ServingRequest adRequest, List<Integer> expIds) {
        List<Placement> placements = adRequest.getPlacements();
        String appId = adRequest.getAppId();

        boolean isValidKey = true;
        for (Placement placement : placements) {
            PlacementEntity placementEntity = placementCache.getPlacementEntity(placement.getId());
            if (placementEntity == null || !placementEntity.getAppId().equals(appId)) {
                isValidKey = false;
                break;
            }
        }

        log.info("Valid key? : {}", isValidKey);

        if (isValidKey) {
            int hmdId = adRequest.getHmdId();

            ArrayList<AdgroupEntity> adgroupsForHmd = new ArrayList<>(adgroupCache.getAdgroupsForHmd(hmdId));
            log.info("Candidate adgroups for the HMD: {}", adgroupsForHmd);

            List<AdgroupEntity> adgroupsWithBudget = adgroupsForHmd.stream().filter(x ->
                    x.getTodayBurn() < x.getDailyBudget() &&
                            x.getTotalBurn() < x.getTotalBudget() &&
                            x.getCmpTodayBurn() < x.getCmpDailyBudget() &&
                            x.getCmpTotalBurn() < x.getCmpTotalBudget()

            ).collect(Collectors.toList());
            log.info("Candidates having budget: {}", adgroupsWithBudget);

            adgroupsWithBudget.sort(Comparator.comparingDouble(x ->
                    x.getPricingModel() == PricingModel.CPC ? -x.getBid() * defaultCtr : -x.getBid())); // reverse sort

            List<Impression> topAds = getTopAds(adgroupsWithBudget, placements.size());
            // Assign ads to a placement
            Map<String, Impression> adsMap = new HashMap<>();
            for (int i = 0; i < topAds.size(); i++) { // At most as many top Ads as placements
                adsMap.put(placements.get(i).getId(), topAds.get(i));
            }
            return new InternalAdResponse(adsMap.size() > 0 ? ResponseCode.SERVED : ResponseCode.NO_AD, HttpStatus.SC_OK, expIds, adsMap);
        } else {
            log.info("Invalid app or placementId");
            return new InternalAdResponse(ResponseCode.BAD_REQUEST, HttpStatus.SC_BAD_REQUEST, expIds, new HashMap<>());
        }
    }

    private List<Impression> getTopAds(List<AdgroupEntity> adgroupsForHmd, int adsToSelect) {
        List<Impression> ads = new ArrayList<>();
        int adsSelected = 0;

        for (AdgroupEntity adgroupEntity : adgroupsForHmd) {
            Set<AdEntity> adsForAdgroup = adCache.getAdsForAdgroup(adgroupEntity.getId());
            log.info("Ads available for adgroup {}: {}", adgroupEntity.getId(), adsForAdgroup);
            for (AdEntity ad : adsForAdgroup) {
                if (adsSelected == adsToSelect) {
                    return ads;
                }
                Impression impression = new Impression(UUID.randomUUID().toString(),
                        adgroupEntity,
                        ad,
                        adgroupEntity.getBid(),
                        adgroupEntity.getBid() * 0.6);
                ads.add(impression);
                adsSelected++;
            }
        }
        return ads;
    }

}
