package com.chymeravr.serving.workers.adselector;

import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.entities.Impression;
import com.chymeravr.serving.entities.cache.AdEntity;
import com.chymeravr.serving.entities.cache.AdgroupEntity;
import com.chymeravr.serving.workers.validator.ValidatedRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * Created by rubbal on 23/3/17.
 */
@Slf4j
@Data
public class AdSelector {
    private final AdCache adCache;
    private final double publisherShare = 0.4;

    public List<Impression> getValidAds(List<AdgroupEntity> selectedAdgroups,
                                        ValidatedRequest validatedRequest) {
        List<Impression> ads = new ArrayList<>();
        int sdkVersion = validatedRequest.getRequest().getSdkVersion();
        for (AdgroupEntity adgroupEntity : selectedAdgroups) {
            // Get ads which satisfy the min sdk version.
            Set<AdEntity> adsForAdgroup = adCache.queryEntities(
                    and(
                            equal(AdEntity.ADGROUP_ID, adgroupEntity.getId()),
                            lessThanOrEqualTo(AdEntity.MIN_SDK, sdkVersion)
                    )
            );

            ArrayList<AdEntity> randomizedAds = new ArrayList<>(adsForAdgroup);
            Collections.shuffle(randomizedAds);
            log.info("Ads available for adgroup {}: {}", adgroupEntity.getId(), randomizedAds);
            for (AdEntity ad : randomizedAds) {
                Impression impression = new Impression(UUID.randomUUID().toString(),
                        adgroupEntity,
                        ad,
                        adgroupEntity.getBid(),
                        adgroupEntity.getBid() * publisherShare);
                ads.add(impression);
            }
        }
        return ads;
    }
}
