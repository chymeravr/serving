package com.chymeravr.guice;

import com.chymeravr.CacheName;
import com.chymeravr.ad.AdCache;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.placement.PlacementCache;
import com.chymeravr.utils.Clock;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by rubbal on 16/1/17.
 */
public class CacheModule extends AbstractModule {

    private final int defaultRefreshTimeSeconds;
    private final Configuration configuration;

    public CacheModule(String configFilePath) throws ConfigurationException {
        this.configuration = new PropertiesConfiguration(configFilePath);
        this.defaultRefreshTimeSeconds = configuration.getInt("defaultRefreshTimeSeconds");
    }

    protected void configure() {

    }

    @Provides
    @Singleton
    DataSource providesDataSource() {
        HikariConfig config = new HikariConfig(configuration.getString("jdbcConfigFile"));
        return new HikariDataSource(config);
    }

    @Provides
    @Singleton
    MetricRegistry providesMetricRegistry() {
        return new MetricRegistry();
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
    AdgroupCache providesAdgroupCache(DataSource dataSource,
                                      MetricRegistry metricRegistry,
                                      Clock clock) throws Exception {
        return new AdgroupCache(CacheName.AdgroupCache, dataSource, metricRegistry, defaultRefreshTimeSeconds, clock);
    }

    @Provides
    @Singleton
    PlacementCache providesPlacementCache(DataSource dataSource,
                                          MetricRegistry metricRegistry,
                                          Clock clock) throws Exception {
        return new PlacementCache(CacheName.PlacementCache, dataSource, metricRegistry, defaultRefreshTimeSeconds, clock);
    }

    @Provides
    @Singleton
    AdCache providesAdCache(DataSource dataSource,
                            MetricRegistry metricRegistry,
                            Clock clock) throws Exception {
        return new AdCache(CacheName.AdCache, dataSource, metricRegistry, defaultRefreshTimeSeconds, clock);
    }
}
