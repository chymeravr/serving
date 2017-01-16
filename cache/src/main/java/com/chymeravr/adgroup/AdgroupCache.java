package com.chymeravr.adgroup;

import com.chymeravr.RepositoryName;
import com.chymeravr.enums.Pricing;
import com.chymeravr.generic.RefreshableDbCache;
import com.chymeravr.utils.Clock;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.chymeravr.serving_dao.Tables.*;

/**
 * Created by rubbal on 12/1/17.
 */
public class AdgroupCache extends RefreshableDbCache<String, AdgroupEntity> {
    public AdgroupCache(RepositoryName name,
                        DataSource connectionPool,
                        MetricRegistry metricRegistry,
                        int refreshTimeSeconds,
                        Clock clock) throws SQLException {
        super(name, connectionPool, metricRegistry, refreshTimeSeconds, clock);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 5, TimeUnit.SECONDS);
    }

    public ImmutableMap<String, AdgroupEntity> load(Connection connection, Map<String, AdgroupEntity> currentEntities) {
        ImmutableMap.Builder<String, AdgroupEntity> mapBuilder = ImmutableMap.builder();
        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record16<UUID, Double, Date, Date, Double, Double, Double, Double, Integer, Double, Double, Double, Double, Integer, Integer, Integer>>
                    result = create
                    .select(
                            ADVERTISER_ADGROUP.ID,
                            ADVERTISER_ADGROUP.BID,
                            ADVERTISER_ADGROUP.STARTDATE,
                            ADVERTISER_ADGROUP.ENDDATE,
                            ADVERTISER_ADGROUP.TOTALBUDGET,
                            ADVERTISER_ADGROUP.DAILYBUDGET,
                            ADVERTISER_ADGROUP.TOTALBURN,
                            ADVERTISER_ADGROUP.TODAYBURN,
                            ADVERTISER_ADGROUP.PRICING_ID,
                            ADVERTISER_CAMPAIGN.TOTALBUDGET,
                            ADVERTISER_CAMPAIGN.DAILYBUDGET,
                            ADVERTISER_CAMPAIGN.TOTALBURN,
                            ADVERTISER_CAMPAIGN.TODAYBURN,
                            ADVERTISER_TARGETING.HMD_ID,
                            ADVERTISER_TARGETING.OS_ID,
                            ADVERTISER_TARGETING.RAM
                    )
                    .from(ADVERTISER_ADGROUP)
                    .join(ADVERTISER_ADGROUP_TARGETING).on(ADVERTISER_ADGROUP.ID.equal(ADVERTISER_ADGROUP_TARGETING.ADGROUP_ID))
                    .join(ADVERTISER_TARGETING).on(ADVERTISER_TARGETING.ID.equal(ADVERTISER_ADGROUP_TARGETING.TARGETING_ID))
                    .join(ADVERTISER_CAMPAIGN).on(ADVERTISER_ADGROUP.CAMPAIGN_ID.equal(ADVERTISER_CAMPAIGN.ID))
                    .fetch();

            for (Record record : result) {
                String adgroupId = record.get(ADVERTISER_ADGROUP.ID).toString();
                AdgroupEntity.AdgroupEntityBuilder adgroupBuilder = AdgroupEntity.builder();
                adgroupBuilder.id(record.get(ADVERTISER_ADGROUP.ID).toString());
                adgroupBuilder.bid(record.get(ADVERTISER_ADGROUP.BID));
                adgroupBuilder.totalBudget(record.get(ADVERTISER_ADGROUP.TOTALBUDGET));
                adgroupBuilder.dailyBudget(record.get(ADVERTISER_ADGROUP.DAILYBUDGET));
                adgroupBuilder.totalBurn(record.get(ADVERTISER_ADGROUP.TOTALBURN));
                adgroupBuilder.todayBurn(record.get(ADVERTISER_ADGROUP.TODAYBURN));
                adgroupBuilder.cmpTotalBudget(record.get(ADVERTISER_CAMPAIGN.TOTALBUDGET));
                adgroupBuilder.cmpDailyBudget(record.get(ADVERTISER_CAMPAIGN.DAILYBUDGET));
                adgroupBuilder.cmpTotalBurn(record.get(ADVERTISER_CAMPAIGN.TOTALBURN));
                adgroupBuilder.cmpTodayBurn(record.get(ADVERTISER_CAMPAIGN.TODAYBURN));
                adgroupBuilder.pricingId(Pricing.getPricing(record.get(ADVERTISER_ADGROUP.PRICING_ID)));
                adgroupBuilder.hmdId(record.get(ADVERTISER_TARGETING.HMD_ID));
                adgroupBuilder.osId(record.get(ADVERTISER_TARGETING.OS_ID));
                adgroupBuilder.minRam(record.get(ADVERTISER_TARGETING.RAM));
                mapBuilder.put(adgroupId, adgroupBuilder.build());
            }
            return mapBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
