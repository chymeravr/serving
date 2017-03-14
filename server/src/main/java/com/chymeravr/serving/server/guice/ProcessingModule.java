package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.cache.placement.PlacementCache;
import com.chymeravr.serving.logging.EventLogger;
import com.chymeravr.serving.logging.NoOpLogger;
import com.chymeravr.serving.logging.ResponseLogger;
import com.chymeravr.serving.processing.adfetcher.AdFetcher;
import com.chymeravr.serving.processing.rqhandler.V1EntryPoint;
import com.chymeravr.serving.processing.rqhandler.serdes.RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.serdes.ResponseSerializer;
import com.chymeravr.serving.processing.rqhandler.serdes.json.V1RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.serdes.json.V1ResponseSerializer;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Data;
import org.apache.commons.configuration.Configuration;

/**
 * Created by rubbal on 19/1/17.
 */
@Data
public class ProcessingModule extends AbstractModule {
    private final Configuration configuration;

    protected void configure() {

    }


    @Provides
    @Named("devMode")
    @Singleton
    Boolean providesDevMode() {
        return configuration.getBoolean("devMode");
    }

    @Provides
    @Singleton
    AdFetcher providesAdFetcher(AdgroupCache adgroupCache, AdCache adCache, PlacementCache placementCache) {
        return new AdFetcher(adgroupCache, adCache, placementCache, configuration.getDouble("defaultCtr"));
    }

    @Provides
    @Singleton
    RequestDeserializer providesDes() {
        return new V1RequestDeserializer();
    }

    @Provides
    @Singleton
    ResponseSerializer providesSer() {
        return new V1ResponseSerializer();
    }


    @Provides
    @Singleton
    ResponseLogger providesEventLogger(@Named("devMode") boolean devMode) {
        return devMode ? new NoOpLogger() : new EventLogger(configuration.subset("kafka"));
    }


    @Provides
    @Singleton
    V1EntryPoint providesV1EntryPoint(RequestDeserializer deserializer,
                                      ResponseSerializer serializer,
                                      AdFetcher adFetcher,
                                      ResponseLogger responseLogger,
                                      MetricRegistry metricRegistry) {
        return new V1EntryPoint(deserializer,
                serializer,
                adFetcher,
                responseLogger,
                configuration.getString("kafkaTopicName"),
                metricRegistry
        );
    }
}
