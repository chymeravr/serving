package com.chymeravr.ad;

import com.chymeravr.CacheName;
import com.chymeravr.generic.RefreshableDbCache;
import com.chymeravr.utils.Clock;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

import static com.chymeravr.serving_dao.Tables.ADVERTISER_AD;
import static com.chymeravr.serving_dao.Tables.ADVERTISER_ADGROUP;

/**
 * Created by rubbal on 12/1/17.
 */
public class AdCache extends RefreshableDbCache<String, AdEntity> {
    public AdCache(CacheName name,
                   DataSource connectionPool,
                   MetricRegistry metricRegistry,
                   int refreshTimeSeconds,
                   Clock clock) throws Exception {
        super(name, connectionPool, metricRegistry, refreshTimeSeconds, clock);
    }

    private ImmutableMap<String, Set<AdEntity>> adsForAdgroup;
    public ImmutableMap<String, AdEntity> load(Connection connection, Map<String, AdEntity> currentEntities) {
        ImmutableMap.Builder<String, AdEntity> mapBuilder = ImmutableMap.builder();
        Map<String, Set<AdEntity>> newAdsForAdgroup = new HashMap<>();

        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record4<UUID, UUID, String, Boolean>> result = create
                    .select(
                            ADVERTISER_ADGROUP.ID,
                            ADVERTISER_AD.ID,
                            ADVERTISER_AD.CREATIVE,
                            ADVERTISER_AD.STATUS
                    )
                    .from(ADVERTISER_ADGROUP)
                    .join(ADVERTISER_AD).on(ADVERTISER_ADGROUP.ID.equal(ADVERTISER_AD.ADGROUP_ID))
                    .fetch();

            for (Record record : result) {
                boolean status = record.get(ADVERTISER_AD.STATUS);

                if (!status) {
                    continue;
                }

                String adgroupId = record.get(ADVERTISER_ADGROUP.ID).toString();
                String adId = record.get(ADVERTISER_AD.ID).toString();
                String creativeUrl = record.get(ADVERTISER_AD.CREATIVE);

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
