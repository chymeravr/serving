package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.cache.CacheName;
import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.cache.placement.PlacementCache;
import com.chymeravr.serving.cache.utils.Clock;
import com.chymeravr.serving.dbconnector.ConnectionFactory;
import com.chymeravr.serving.dbconnector.PsqlUnpooledConnectionFactory;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

import java.sql.SQLException;

/**
 * Created by rubbal on 16/1/17.
 */
public class CacheModule extends AbstractModule {
    public static final String METRIC_REGISTRY_NAME = "app";
    private final int defaultRefreshTimeSeconds;
    private final Configuration configuration;

    public CacheModule(Configuration configuration) throws ConfigurationException {
        this.configuration = configuration;
        this.defaultRefreshTimeSeconds = configuration.getInt("defaultRefreshTimeSeconds");
    }

    protected void configure() {

    }

    @Provides
    @Singleton
    ConnectionFactory providesDataSource() throws ConfigurationException, SQLException, ClassNotFoundException {
        String jdbcConfigFile = configuration.getString("jdbcConfigFile");
        return new PsqlUnpooledConnectionFactory(jdbcConfigFile);
    }

    @Provides
    @Singleton
    MetricRegistry providesMetricRegistry() {
        MetricRegistry metricRegistry = new MetricRegistry();
        SharedMetricRegistries.add(METRIC_REGISTRY_NAME, metricRegistry);
        return metricRegistry;
    }

    @Provides
    @Singleton
    Clock providesClock() {
        return new Clock() {
            public long currentTimeMillis() {
                return System.currentTimeMillis();
            }
        };
    }

    @Provides
    @Singleton
    AdgroupCache providesAdgroupCache(ConnectionFactory connectionFactory,
                                      MetricRegistry metricRegistry,
                                      Clock clock) throws Exception {
        return new AdgroupCache(CacheName.AdgroupCache, connectionFactory, metricRegistry, defaultRefreshTimeSeconds, clock);
    }

    @Provides
    @Singleton
    PlacementCache providesPlacementCache(ConnectionFactory connectionFactory,
                                          MetricRegistry metricRegistry,
                                          Clock clock) throws Exception {
        return new PlacementCache(CacheName.PlacementCache, connectionFactory, metricRegistry, defaultRefreshTimeSeconds, clock);
    }

    @Provides
    @Singleton
    AdCache providesAdCache(ConnectionFactory connectionFactory,
                            MetricRegistry metricRegistry,
                            Clock clock) throws Exception {
        return new AdCache(CacheName.AdCache, connectionFactory, metricRegistry, defaultRefreshTimeSeconds, clock);
    }


}
