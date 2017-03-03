package com.chymeravr.serving.cache.adgroup;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.generic.RefreshableDbCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.dao.Tables;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.chymeravr.serving.entities.cache.AdgroupEntity;
import com.chymeravr.serving.enums.PricingUtils;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableSet;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static com.chymeravr.serving.dao.Tables.*;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.or;

/**
 * Created by rubbal on 12/1/17.
 */
@Slf4j
public class AdgroupCache extends RefreshableDbCache<AdgroupEntity> {
    public AdgroupCache(CacheName name,
                        ConnectionFactory connectionFactory,
                        MetricRegistry metricRegistry,
                        int refreshTimeSeconds,
                        Clock clock) throws Exception {
        super(name, connectionFactory, metricRegistry, refreshTimeSeconds, clock);
    }

    @Override
    public IndexedCollection<AdgroupEntity> getEmptyIndexedCollection() {
        ConcurrentIndexedCollection<AdgroupEntity> entities = new ConcurrentIndexedCollection<>();
        entities.addIndex(HashIndex.onAttribute(AdgroupEntity.ID));
        entities.addIndex(HashIndex.onAttribute(AdgroupEntity.HMD));

        return entities;
    }

    @Override
    public Set<AdgroupEntity> load(Connection connection, IndexedCollection<AdgroupEntity> currentEntities) {
        ImmutableSet.Builder<AdgroupEntity> entityBuilder = ImmutableSet.builder();
        Date sqlDate = new Date(new java.util.Date().getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.DATE, -1);
        Date yesterdayDate = new Date(c.getTime().getTime());

        try {
            DSLContext context = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record20<UUID, UUID, Double, Date, Date, Double, Double, Double, Double, Integer, Boolean, Double, Double, Double, Double, Boolean, Integer, Integer, Integer, String>>
                    result = context.select(
                    CHYM_USER_PROFILE.ID,
                    ADVERTISER_ADGROUP.ID,
                    ADVERTISER_ADGROUP.BID,
                    ADVERTISER_ADGROUP.STARTDATE,
                    ADVERTISER_ADGROUP.ENDDATE,
                    ADVERTISER_ADGROUP.TOTALBUDGET,
                    ADVERTISER_ADGROUP.DAILYBUDGET,
                    ADVERTISER_ADGROUP.TOTALBURN,
                    ADVERTISER_ADGROUP.TODAYBURN,
                    ADVERTISER_ADGROUP.PRICING_ID,
                    ADVERTISER_ADGROUP.STATUS,
                    ADVERTISER_CAMPAIGN.TOTALBUDGET,
                    ADVERTISER_CAMPAIGN.DAILYBUDGET,
                    ADVERTISER_CAMPAIGN.TOTALBURN,
                    ADVERTISER_CAMPAIGN.TODAYBURN,
                    ADVERTISER_CAMPAIGN.STATUS,
                    ADVERTISER_TARGETING.HMD_ID,
                    ADVERTISER_TARGETING.OS_ID,
                    ADVERTISER_TARGETING.RAM,
                    ADVERTISER_CAMPAIGN.APPNAME
            )
                    .from(ADVERTISER_ADGROUP)
                    .leftJoin(ADVERTISER_ADGROUP_TARGETING).on(ADVERTISER_ADGROUP.ID.equal(ADVERTISER_ADGROUP_TARGETING.ADGROUP_ID))
                    .leftJoin(ADVERTISER_TARGETING).on(ADVERTISER_TARGETING.ID.equal(ADVERTISER_ADGROUP_TARGETING.TARGETING_ID))
                    .join(ADVERTISER_CAMPAIGN).on(ADVERTISER_ADGROUP.CAMPAIGN_ID.equal(ADVERTISER_CAMPAIGN.ID))
                    .join(CHYM_USER_PROFILE).on(ADVERTISER_CAMPAIGN.USER_ID.eq(CHYM_USER_PROFILE.USER_ID))
                    .where(Tables.ADVERTISER_ADGROUP.STARTDATE.lessOrEqual(sqlDate)
                            .and(Tables.ADVERTISER_ADGROUP.ENDDATE.greaterThan(yesterdayDate))
                            .and(Tables.ADVERTISER_CAMPAIGN.STARTDATE.lessOrEqual(sqlDate))
                            .and(Tables.ADVERTISER_CAMPAIGN.ENDDATE.greaterThan(yesterdayDate))
                            .and(Tables.CHYM_USER_PROFILE.ADVERTISING_FUNDS.greaterThan(Tables.CHYM_USER_PROFILE.ADVERTISING_BURN))
                    )
                    .fetch();

            for (Record record : result) {
                try {
                    Boolean status = record.get(ADVERTISER_ADGROUP.STATUS) && record.get(ADVERTISER_CAMPAIGN.STATUS);

                    // Can put this in SQL but we might need this for delta updates
                    if (!status) {
                        continue;
                    }

                    String adgroupId = record.get(ADVERTISER_ADGROUP.ID).toString();

                    Integer hmdId = record.get(ADVERTISER_TARGETING.HMD_ID);
                    if (hmdId == null) hmdId = -1;

                    Integer osId = record.get(ADVERTISER_TARGETING.OS_ID);
                    if (osId == null) osId = -1;

                    Integer minRam = record.get(ADVERTISER_TARGETING.RAM);
                    if (minRam == null) minRam = 0;

                    AdgroupEntity.AdgroupEntityBuilder adgroupBuilder = AdgroupEntity.builder();
                    adgroupBuilder.id(record.get(ADVERTISER_ADGROUP.ID).toString());
                    adgroupBuilder.advertiserId(record.get(CHYM_USER_PROFILE.ID).toString());
                    adgroupBuilder.bid(record.get(ADVERTISER_ADGROUP.BID));
                    adgroupBuilder.totalBudget(record.get(ADVERTISER_ADGROUP.TOTALBUDGET));
                    adgroupBuilder.dailyBudget(record.get(ADVERTISER_ADGROUP.DAILYBUDGET));
                    adgroupBuilder.totalBurn(record.get(ADVERTISER_ADGROUP.TOTALBURN));
                    adgroupBuilder.todayBurn(record.get(ADVERTISER_ADGROUP.TODAYBURN));
                    adgroupBuilder.cmpTotalBudget(record.get(ADVERTISER_CAMPAIGN.TOTALBUDGET));
                    adgroupBuilder.cmpDailyBudget(record.get(ADVERTISER_CAMPAIGN.DAILYBUDGET));
                    adgroupBuilder.cmpTotalBurn(record.get(ADVERTISER_CAMPAIGN.TOTALBURN));
                    adgroupBuilder.cmpTodayBurn(record.get(ADVERTISER_CAMPAIGN.TODAYBURN));
                    adgroupBuilder.pricingModel(PricingUtils.getPricing(record.get(ADVERTISER_ADGROUP.PRICING_ID)));
                    adgroupBuilder.appName(record.get(ADVERTISER_CAMPAIGN.APPNAME));
                    adgroupBuilder.osId(osId);
                    adgroupBuilder.minRam(minRam);
                    adgroupBuilder.hmdId(hmdId);

                    AdgroupEntity adgroup = adgroupBuilder.build();
                    entityBuilder.add(adgroup);
                } catch (Exception e) {
                    log.error("Unable to load entity: {}", record, e);
                }
            }
            return entityBuilder.build();
        } catch (Exception e) {
            log.error("Unable to refresh repo", e);
            throw new RuntimeException(e);
        }
    }

    public Set<AdgroupEntity> getAdgroupsForHmd(int hmdId) {
        return this.queryEntities(or(
                equal(AdgroupEntity.HMD, hmdId),
                equal(AdgroupEntity.HMD, -1)) // All targeted
        );
    }
}
