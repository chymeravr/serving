package com.chymeravr.serving.cache.placement;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.generic.RefreshableDbCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.enums.AppStore;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.chymeravr.serving.dao.Tables.PUBLISHER_APP;
import static com.chymeravr.serving.dao.tables.PublisherPlacement.PUBLISHER_PLACEMENT;


/**
 * Created by rubbal on 12/1/17.
 */
public class PlacementCache extends RefreshableDbCache<String, PlacementEntity> {
    public PlacementCache(CacheName name,
                          DataSource connectionPool,
                          MetricRegistry metricRegistry,
                          int refreshTimeSeconds,
                          Clock clock) throws Exception {
        super(name, connectionPool, metricRegistry, refreshTimeSeconds, clock);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 5, TimeUnit.SECONDS);
    }

    public ImmutableMap<String, PlacementEntity> load(Connection connection, Map<String, PlacementEntity> currentEntities) {
        ImmutableMap.Builder<String, PlacementEntity> mapBuilder = ImmutableMap.builder();
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
                mapBuilder.put(placementId, placementEntity);
            }
            return mapBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
