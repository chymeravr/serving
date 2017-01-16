package com.chymeravr.generic;

import com.chymeravr.RepositoryName;
import com.chymeravr.utils.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractScheduledService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rubbal on 16/1/17.
 */
public abstract class RefreshableDbCache<K, V> extends AbstractScheduledService {
    private final Counter updatesAttemped;
    private final Counter updatesSucceeded;
    private final Counter updatesFailed;

    // Mutable state
    private ImmutableMap<K, V> entities;
    private long lastAttemptedUpdateTime;
    private long lastSuccessfulUpdateTime;

    // Immutable state
    private final RepositoryName repositoryName;
    private final DataSource connectionPool;
    private final int refreshTimeSeconds;
    private final Clock clock;

    public RefreshableDbCache(RepositoryName name,
                              DataSource connectionPool,
                              MetricRegistry metricRegistry,
                              int refreshTimeSeconds,
                              Clock clock) throws SQLException {
        // Immutable state
        this.repositoryName = name;
        this.connectionPool = connectionPool;
        this.refreshTimeSeconds = refreshTimeSeconds;
        this.clock = clock;

        // Internal state
        this.entities = ImmutableMap.of();
        this.lastAttemptedUpdateTime = 0;
        this.lastSuccessfulUpdateTime = 0;

        // Metrics
        this.updatesAttemped = metricRegistry.counter("updatesAttempted");
        this.updatesSucceeded = metricRegistry.counter("updatesSucceeded");
        this.updatesFailed = metricRegistry.counter("updatesFailed");

        metricRegistry.register(getMetricName("entityCount"), (Gauge<Integer>) () -> entities.size());
        metricRegistry.register(getMetricName("lastAttemptedUpdate"), (Gauge<Long>) () -> this.lastAttemptedUpdateTime);
        metricRegistry.register(getMetricName("lastSuccessfulUpdate"), (Gauge<Long>) () -> this.lastSuccessfulUpdateTime);
    }

    public abstract ImmutableMap<K, V> load(Connection connection, Map<K, V> currentEntities);

    private String getMetricName(String metricName) {
        return String.format("cache.stats.%s.%s", repositoryName.toString(), metricName);
    }

    @Override
    protected void runOneIteration() throws Exception {
        try (Connection connection = this.connectionPool.getConnection()) {
            this.updatesAttemped.inc();
            this.lastAttemptedUpdateTime = this.clock.currentTimeMillis();
            this.entities = load(connection, this.entities);
            this.updatesSucceeded.inc();
        } catch (Exception e) {
            this.updatesFailed.inc();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, this.refreshTimeSeconds, TimeUnit.SECONDS);
    }
}
