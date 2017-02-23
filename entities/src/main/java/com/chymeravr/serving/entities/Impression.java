package com.chymeravr.serving.entities;

import com.chymeravr.schemas.serving.ImpressionInfo;
import com.chymeravr.serving.entities.cache.AdEntity;
import com.chymeravr.serving.entities.cache.AdgroupEntity;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by rubbal on 22/2/17.
 */
@Data
public class Impression {
    @NonNull
    private final String servingId;
    @NonNull
    private final AdgroupEntity adgroup;
    @NonNull
    private final AdEntity ad;
    private final double costPrice;
    private final double sellingPrice;

    public ImpressionInfo getImpressionInfo() {
        ImpressionInfo impressionInfo = new ImpressionInfo(
                servingId,
                adgroup.getAdvertiserId(),
                adgroup.getId(),
                ad.getId(),
                costPrice,
                sellingPrice,
                ad.getUrl(),
                adgroup.getPricingModel()
        );
        if (ad.getLandingPage() != null) impressionInfo.setClickUrl(ad.getLandingPage());
        return impressionInfo;
    }
}
