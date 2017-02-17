package com.chymeravr.serving.cache.ad;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.generic.RefreshableDbCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.cache.utils.DateUtils;
import com.chymeravr.serving.dao.Tables;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.*;

/**
 * Created by rubbal on 12/1/17.
 */
@Slf4j
public class AdCache extends RefreshableDbCache<String, AdEntity> {
    public AdCache(CacheName name,
                   ConnectionFactory connectionFactory,
                   MetricRegistry metricRegistry,
                   int refreshTimeSeconds,
                   Clock clock) throws Exception {
        super(name, connectionFactory, metricRegistry, refreshTimeSeconds, clock);
    }

    private ImmutableMap<String, Set<AdEntity>> adsForAdgroup;

    public ImmutableMap<String, AdEntity> load(Connection connection, Map<String, AdEntity> currentEntities) {
        ImmutableMap.Builder<String, AdEntity> mapBuilder = ImmutableMap.builder();
        Map<String, Set<AdEntity>> newAdsForAdgroup = new HashMap<>();

        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record4<UUID, UUID, String, Boolean>> result = create
                    .select(
                            Tables.ADVERTISER_ADGROUP.ID,
                            Tables.ADVERTISER_AD.ID,
                            Tables.ADVERTISER_AD.CREATIVE,
                            Tables.ADVERTISER_AD.STATUS
                    )
                    .from(Tables.ADVERTISER_ADGROUP)
                    .join(Tables.ADVERTISER_AD).on(Tables.ADVERTISER_ADGROUP.ID.equal(Tables.ADVERTISER_AD.ADGROUP_ID))
                    .fetch();

            for (Record record : result) {
                boolean status = record.get(Tables.ADVERTISER_AD.STATUS);

                if (!status) {
                    continue;
                }

                String adgroupId = record.get(Tables.ADVERTISER_ADGROUP.ID).toString();

                String adId = record.get(Tables.ADVERTISER_AD.ID).toString();
                String creativeUrl = record.get(Tables.ADVERTISER_AD.CREATIVE);

                AdEntity adEntity = new AdEntity(adId, adgroupId, creativeUrl);
                mapBuilder.put(adId, adEntity);

                Set<AdEntity> adEntities = newAdsForAdgroup.get(adgroupId);

                if (adEntities == null) {
                    adEntities = new HashSet<>();
                    adEntities.add(adEntity);
                    newAdsForAdgroup.put(adgroupId, adEntities);
                } else {
                    adEntities.add(adEntity);
                }
            }

            this.adsForAdgroup = ImmutableMap.copyOf(newAdsForAdgroup);
            return mapBuilder.build();
        } catch (Exception e) {
            log.error("Unable to refresh repo", e);
            throw new RuntimeException(e);
        }
    }

    public Set<AdEntity> getAdsForAdgroup(String adgroupId) {
        Set<AdEntity> adEntities = this.adsForAdgroup.get(adgroupId);
        if (adEntities == null) {
            return Collections.emptySet();
        }
        return adEntities;
    }
}
