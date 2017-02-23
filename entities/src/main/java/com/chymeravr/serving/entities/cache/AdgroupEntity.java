package com.chymeravr.serving.entities.cache;

import com.chymeravr.schemas.serving.PricingModel;
import com.googlecode.cqengine.attribute.Attribute;
import lombok.*;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

/**
 * Created by rubbal on 16/1/17.
 */
@EqualsAndHashCode(of = {"id"})
@ToString
@Builder
@Getter
public class AdgroupEntity {

    public static final Attribute<AdgroupEntity, String> ID = attribute(AdgroupEntity::getId);
    public static final Attribute<AdgroupEntity, Integer> HMD = attribute(AdgroupEntity::getHmdId);

    @NonNull
    private final String id;
    private final String advertiserId;
    private final String appName;
    private final double bid;
    private final double totalBudget;
    private final double dailyBudget;
    private final double totalBurn;
    private final double todayBurn;
    private final double cmpTotalBudget;
    private final double cmpDailyBudget;
    private final double cmpTotalBurn;
    private final double cmpTodayBurn;
    @NonNull
    private final PricingModel pricingModel;
    private final Integer hmdId;
    private final Integer osId;
    private final Integer minRam;
}
