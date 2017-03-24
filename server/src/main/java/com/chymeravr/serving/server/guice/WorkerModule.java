package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.cache.placement.PlacementCache;
import com.chymeravr.serving.logging.EventLogger;
import com.chymeravr.serving.logging.NoOpLogger;
import com.chymeravr.serving.logging.ResponseLogger;
import com.chymeravr.serving.workers.adgroupselector.AdgroupSelector;
import com.chymeravr.serving.workers.adranker.AdRanker;
import com.chymeravr.serving.workers.adselector.AdSelector;
import com.chymeravr.serving.workers.responsewriter.ResponseWriter;
import com.chymeravr.serving.workers.validator.RequestValidator;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.configuration.Configuration;

/**
 * Created by rubbal on 21/3/17.
 */
public class WorkerModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    RequestValidator providesRequestValidator(PlacementCache placementCache) {
        return new RequestValidator(placementCache);
    }

    @Provides
    @Singleton
    AdgroupSelector providesAdSelector(AdgroupCache adgroupCache,
                                       @Named("defaultCtr") Double defaultCtr) {
        return new AdgroupSelector(adgroupCache, defaultCtr);
    }

    @Provides
    @Named("devMode")
    @Singleton
    Boolean providesDevMode(Configuration configuration) {
        return configuration.getBoolean("devMode");
    }


    @Provides
    @Singleton
    AdSelector providesAdSelector(AdCache adCache) {
        return new AdSelector(adCache);
    }

    @Provides
    @Singleton
    AdRanker providesAdRanker() {
        return new AdRanker();
    }

    @Provides
    @Singleton
    ResponseWriter providesResponseWriter(ResponseLogger responseLogger, @Named("topicName") String topicName) {
        return new ResponseWriter(responseLogger, topicName);
    }

    @Provides
    @Singleton
    ResponseLogger providesEventLogger(@Named("devMode") boolean devMode, Configuration configuration) {
        return devMode ? new NoOpLogger() : new EventLogger(configuration.subset("kafka"));
    }

    @Provides
    @Singleton
    @Named("LatencyTimer")
    Timer providesTimer(MetricRegistry metricRegistry) {
        return metricRegistry.timer("serving.latencyTimer");
    }
}
