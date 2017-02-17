package com.chymeravr.serving.cache.adgroup;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.generic.RefreshableDbCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.dao.Tables;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.chymeravr.serving.enums.PricingUtils;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.Date;
import java.util.*;

import static com.chymeravr.serving.dao.Tables.*;

/**
 * Created by rubbal on 12/1/17.
 */
@Slf4j
public class AdgroupCache extends RefreshableDbCache<String, AdgroupEntity> {
    public AdgroupCache(CacheName name,
                        ConnectionFactory connectionFactory,
                        MetricRegistry metricRegistry,
                        int refreshTimeSeconds,
                        Clock clock) throws Exception {
        super(name, connectionFactory, metricRegistry, refreshTimeSeconds, clock);
    }

    private ImmutableMap<Integer, Set<AdgroupEntity>> hmdMapping;

    public ImmutableMap<String, AdgroupEntity> load(Connection connection, Map<String, AdgroupEntity> currentEntities) {
        ImmutableMap.Builder<String, AdgroupEntity> mapBuilder = ImmutableMap.builder();
        Map<Integer, Set<AdgroupEntity>> newHmdMappings = new HashMap<>();
        Date sqlDate = new Date(new java.util.Date().getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.DATE, -1);
        Date yesterdayDate = new Date(c.getTime().getTime());

        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record19<UUID, UUID, Double, Date, Date, Double, Double, Double, Double, Integer, Boolean, Double, Double, Double, Double, Boolean, Integer, Integer, Integer>>
                    result = create
                    .select(
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
                            ADVERTISER_TARGETING.RAM
                    )
                    .from(ADVERTISER_ADGROUP)
                    .join(ADVERTISER_ADGROUP_TARGETING).on(ADVERTISER_ADGROUP.ID.equal(ADVERTISER_ADGROUP_TARGETING.ADGROUP_ID))
                    .join(ADVERTISER_TARGETING).on(ADVERTISER_TARGETING.ID.equal(ADVERTISER_ADGROUP_TARGETING.TARGETING_ID))
                    .join(ADVERTISER_CAMPAIGN).on(ADVERTISER_ADGROUP.CAMPAIGN_ID.equal(ADVERTISER_CAMPAIGN.ID))
                    .join(CHYM_USER_PROFILE).on(ADVERTISER_CAMPAIGN.USER_ID.eq(CHYM_USER_PROFILE.USER_ID))
                    .where(Tables.ADVERTISER_ADGROUP.STARTDATE.lessOrEqual(sqlDate)
                            .and(Tables.ADVERTISER_ADGROUP.ENDDATE.greaterThan(yesterdayDate))
                            .and(Tables.ADVERTISER_CAMPAIGN.STARTDATE.lessOrEqual(sqlDate))
                            .and(Tables.ADVERTISER_CAMPAIGN.ENDDATE.greaterThan(yesterdayDate))
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
                    adgroupBuilder.osId(record.get(ADVERTISER_TARGETING.OS_ID));
                    adgroupBuilder.minRam(record.get(ADVERTISER_TARGETING.RAM));
                    adgroupBuilder.hmdId(hmdId);

                    AdgroupEntity adgroup = adgroupBuilder.build();
                    mapBuilder.put(adgroupId, adgroup);

                    Set<AdgroupEntity> adgroupEntities = newHmdMappings.get(hmdId);
                    if (adgroupEntities == null) {
                        adgroupEntities = new HashSet<>();
                        adgroupEntities.add(adgroup);
                        newHmdMappings.put(hmdId, adgroupEntities);
                    } else {
                        adgroupEntities.add(adgroup);
                    }
                } catch (Exception e) {
                    log.error("Unable to load entity: {}", record, e);
                }
            }
            this.hmdMapping = ImmutableMap.copyOf(newHmdMappings);
            return mapBuilder.build();
        } catch (Exception e) {
            log.error("Unable to refresh repo", e);
            throw new RuntimeException(e);
        }
    }

    public Set<AdgroupEntity> getAdgroupsForHmd(int hmdId) {
        Set<AdgroupEntity> adgroupEntities = hmdMapping.get(hmdId);
        Set<AdgroupEntity> untargetedAdgroups = hmdMapping.get(-1);

        // If both empty
        if (adgroupEntities == null && untargetedAdgroups == null) {
            return Collections.emptySet();
        }

        // Guaranteed that one of them is non-null. Create new objects to avoid leaking references.
        // TODO: Return immutable sets
        if (adgroupEntities == null) {
            return new HashSet<>(untargetedAdgroups);
        }

        if (untargetedAdgroups == null) {
            return new HashSet<>(adgroupEntities);
        }

        // If both of them are non-null, create a new set and add them both
        Set<AdgroupEntity> validAdgroups = new HashSet<>();
        validAdgroups.addAll(untargetedAdgroups);
        validAdgroups.addAll(adgroupEntities);
        return validAdgroups;
    }
}
