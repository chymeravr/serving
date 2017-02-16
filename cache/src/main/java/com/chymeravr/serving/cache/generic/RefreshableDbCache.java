package com.chymeravr.serving.cache.generic;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rubbal on 16/1/17.
 */
@Slf4j
public abstract class RefreshableDbCache<K, V> extends AbstractScheduledService {
    private final Counter updatesAttemped;
    private final Counter updatesSucceeded;
    private final Counter updatesFailed;

    // Mutable state
    @Getter
    private ImmutableMap<K, V> entities;
    private long lastAttemptedUpdateTime;
    private long lastSuccessfulUpdateTime;

    // Immutable state
    private final CacheName cacheName;
    private final ConnectionFactory connectionFactory;
    private final int refreshTimeSeconds;
    private final Clock clock;

    public RefreshableDbCache(CacheName name,
                              ConnectionFactory connectionFactory,
                              MetricRegistry metricRegistry,
                              int refreshTimeSeconds,
                              Clock clock) throws Exception {
        // Immutable state
        this.cacheName = name;
        this.connectionFactory = connectionFactory;
        this.refreshTimeSeconds = refreshTimeSeconds;
        this.clock = clock;

        // Internal state
        this.entities = ImmutableMap.of();
        this.lastAttemptedUpdateTime = 0;
        this.lastSuccessfulUpdateTime = 0;

        // Metrics
        this.updatesAttemped = metricRegistry.counter(getMetricName("updatesAttempted"));
        this.updatesSucceeded = metricRegistry.counter(getMetricName("updatesSucceeded"));
        this.updatesFailed = metricRegistry.counter(getMetricName("updatesFailed"));

        metricRegistry.register(getMetricName("entityCount"), (Gauge<Integer>) () -> entities.size());
        metricRegistry.register(getMetricName("lastAttemptedUpdate"), (Gauge<Long>) () -> this.lastAttemptedUpdateTime);
        metricRegistry.register(getMetricName("lastSuccessfulUpdate"), (Gauge<Long>) () -> this.lastSuccessfulUpdateTime);

        // Update once
        runOneIteration();
        this.startAsync();
    }

    public abstract ImmutableMap<K, V> load(Connection connection, Map<K, V> currentEntities);

    private String getMetricName(String metricName) {
        return String.format("cache.stats.%s.%s", cacheName.toString(), metricName);
    }

    @Override
    protected void runOneIteration() throws Exception {
        try (Connection connection = this.connectionFactory.getConnection()) {
            this.updatesAttemped.inc();
            this.lastAttemptedUpdateTime = this.clock.currentTimeMillis();
            this.entities = load(connection, this.entities);
            this.lastSuccessfulUpdateTime = this.lastAttemptedUpdateTime;
            this.updatesSucceeded.inc();
        } catch (Exception e) {
            this.updatesFailed.inc();
            log.error("Unable to refresh {}", this.cacheName, e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        // Ok to set initial delay as refreshTime as we have an update while constructing the repo
        return Scheduler.newFixedDelaySchedule(this.refreshTimeSeconds,
                this.refreshTimeSeconds,
                TimeUnit.SECONDS);
    }
}
