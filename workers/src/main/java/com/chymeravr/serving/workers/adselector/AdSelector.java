package com.chymeravr.serving.workers.adselector;

import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.entities.Impression;
import com.chymeravr.serving.entities.cache.AdEntity;
import com.chymeravr.serving.entities.cache.AdgroupEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Created by rubbal on 23/3/17.
 */
@Slf4j
@Data
public class AdSelector {
    private final AdCache adCache;

    public List<Impression> getValidAds(List<AdgroupEntity> selectedAdgroups) {
        List<Impression> ads = new ArrayList<>();
        for (AdgroupEntity adgroupEntity : selectedAdgroups) {
            Set<AdEntity> adsForAdgroup = adCache.queryEntities(equal(AdEntity.ADGROUP_ID, adgroupEntity.getId()));
            ArrayList<AdEntity> randomizedAds = new ArrayList<>(adsForAdgroup);
            Collections.shuffle(randomizedAds);
            log.info("Ads available for adgroup {}: {}", adgroupEntity.getId(), randomizedAds);
            for (AdEntity ad : randomizedAds) {
                Impression impression = new Impression(UUID.randomUUID().toString(),
                        adgroupEntity,
                        ad,
                        adgroupEntity.getBid(),
                        adgroupEntity.getBid() * 0.6);
                ads.add(impression);
            }
        }
        return ads;
    }
}
