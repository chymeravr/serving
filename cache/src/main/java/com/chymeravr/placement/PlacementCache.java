package com.chymeravr.placement;

import com.chymeravr.CacheName;
import com.chymeravr.enums.AppStore;
import com.chymeravr.generic.RefreshableDbCache;
import com.chymeravr.utils.Clock;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.chymeravr.serving_dao.Tables.PUBLISHER_APP;
import static com.chymeravr.serving_dao.Tables.PUBLISHER_PLACEMENT;

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
