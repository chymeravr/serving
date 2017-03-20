package com.chymeravr.serving.cache.ad;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.generic.RefreshableDbCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.dao.Tables;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.chymeravr.serving.entities.cache.AdEntity;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableSet;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.Set;
import java.util.UUID;

/**
 * Created by rubbal on 12/1/17.
 */
@Slf4j
public class AdCache extends RefreshableDbCache<AdEntity> {
    private static final String CREATIVE_URL_PREFIX = "https://chymerastatic.blob.core.windows.net/creatives/";

    public AdCache(CacheName name,
                   ConnectionFactory connectionFactory,
                   MetricRegistry metricRegistry,
                   int refreshTimeSeconds,
                   Clock clock) throws Exception {
        super(name, connectionFactory, metricRegistry, refreshTimeSeconds, clock);
    }

    public Set<AdEntity> load(Connection connection, IndexedCollection<AdEntity> currentEntities) {
        ImmutableSet.Builder<AdEntity> builder = ImmutableSet.builder();

        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record6<UUID, UUID, String, Boolean, String, Integer>> result = create
                    .select(
                            Tables.ADVERTISER_ADGROUP.ID,
                            Tables.ADVERTISER_AD.ID,
                            Tables.ADVERTISER_AD.CREATIVE,
                            Tables.ADVERTISER_AD.STATUS,
                            Tables.ADVERTISER_AD.LANDINGPAGE,
                            Tables.ADVERTISER_AD.ADTYPE
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
                String creativeUrl = CREATIVE_URL_PREFIX + record.get(Tables.ADVERTISER_AD.CREATIVE);
                String clickUrl = record.get(Tables.ADVERTISER_AD.LANDINGPAGE);
                Integer adType = record.get(Tables.ADVERTISER_AD.ADTYPE);

                AdEntity adEntity = new AdEntity(adId, adgroupId, creativeUrl, clickUrl, adType);
                builder.add(adEntity);
            }

            return builder.build();
        } catch (Exception e) {
            log.error("Unable to refresh repo", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexedCollection<AdEntity> getEmptyIndexedCollection() {
        ConcurrentIndexedCollection<AdEntity> entities = new ConcurrentIndexedCollection<>();
        entities.addIndex(HashIndex.onAttribute(AdEntity.ID));
        entities.addIndex(HashIndex.onAttribute(AdEntity.ADGROUP_ID));

        return entities;
    }
}
