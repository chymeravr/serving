package com.chymeravr.adgroup;

import com.chymeravr.RepositoryName;
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
public class Cache extends RefreshableDbCache<String, AdgroupEntity> {
    public Cache(RepositoryName name,
                 DataSource connectionPool,
                 MetricRegistry metricRegistry,
                 int refreshTimeSeconds,
                 Clock clock) throws SQLException {
        super(name, connectionPool, metricRegistry, refreshTimeSeconds, clock);
    }

//    public static void main(String[] args) {
//        Cache cache = new Cache();
//        cache.startAsync();
//    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 5, TimeUnit.SECONDS);
    }

    public ImmutableMap<String, AdgroupEntity> load(Connection connection, Map<String, AdgroupEntity> currentEntities) {
        ImmutableMap.Builder<String, AdgroupEntity> builder = ImmutableMap.builder();
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
                builder.put(adgroupId, new AdgroupEntity(adgroupId));
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
