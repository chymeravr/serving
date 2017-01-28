package com.chymeravr.serving.cache.adgroup;

import com.chymeravr.serving.enums.Pricing;
import lombok.*;

/**
 * Created by rubbal on 16/1/17.
 */
@EqualsAndHashCode(of = {"id"})
@ToString
@Builder
@Getter
public class AdgroupEntity {
    @NonNull
    private final String id;
    private final String advertiserId;
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
    private final Pricing pricingId;
    private final Integer hmdId;
    private final Integer osId;
    private final Integer minRam;
}
