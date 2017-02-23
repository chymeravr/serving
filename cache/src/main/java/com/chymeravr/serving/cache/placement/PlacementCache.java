package com.chymeravr.serving.cache.placement;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.generic.RefreshableDbCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.chymeravr.serving.entities.cache.PlacementEntity;
import com.chymeravr.serving.enums.AppStore;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableSet;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.Set;
import java.util.UUID;

import static com.chymeravr.serving.dao.Tables.PUBLISHER_APP;
import static com.chymeravr.serving.dao.tables.PublisherPlacement.PUBLISHER_PLACEMENT;


/**
 * Created by rubbal on 12/1/17.
 */
public class PlacementCache extends RefreshableDbCache<PlacementEntity> {
    public PlacementCache(CacheName name,
                          ConnectionFactory connectionFactory,
                          MetricRegistry metricRegistry,
                          int refreshTimeSeconds,
                          Clock clock) throws Exception {
        super(name, connectionFactory, metricRegistry, refreshTimeSeconds, clock);
    }

    public Set<PlacementEntity> load(Connection connection, IndexedCollection<PlacementEntity> currentEntities) {
        ImmutableSet.Builder<PlacementEntity> builder = ImmutableSet.builder();
        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES_9_5);
            Result<Record3<UUID, UUID, Integer>> result = create
                    .select(
                            PUBLISHER_PLACEMENT.ID,
                            PUBLISHER_PLACEMENT.APP_ID,
                            PUBLISHER_APP.APPSTORE_ID
                    )
                    .from(PUBLISHER_PLACEMENT)
                    .join(PUBLISHER_APP).on(PUBLISHER_PLACEMENT.APP_ID.equal(PUBLISHER_APP.ID))
                    .fetch();

            for (Record record : result) {
                String placementId = record.get(PUBLISHER_PLACEMENT.ID).toString();
                PlacementEntity placementEntity = new PlacementEntity(
                        placementId,
                        record.get(PUBLISHER_PLACEMENT.APP_ID).toString(),
                        AppStore.getAppStore(record.get(PUBLISHER_APP.APPSTORE_ID))
                );
                builder.add(placementEntity);
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexedCollection<PlacementEntity> getEmptyIndexedCollection() {
        ConcurrentIndexedCollection<PlacementEntity> entities = new ConcurrentIndexedCollection<>();
        entities.addIndex(HashIndex.onAttribute(PlacementEntity.ID));

        return entities;
    }
}
