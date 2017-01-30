package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.cache.ad.AdCache;
import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.logging.EventLogger;
import com.chymeravr.serving.processing.adfetcher.AdFetcher;
import com.chymeravr.serving.processing.logger.ResponseLogger;
import com.chymeravr.serving.processing.rqhandler.V1EntryPoint;
import com.chymeravr.serving.processing.rqhandler.entities.v1.json.V1RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.entities.v1.json.V1ResponseSerializer;
import com.chymeravr.serving.processing.rqhandler.iface.RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.iface.ResponseSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
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
    @Singleton
    AdFetcher providesAdFetcher(AdgroupCache adgroupCache, AdCache adCache) {
        return new AdFetcher(adgroupCache, adCache);
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
    EventLogger providesEventLogger() {
        return new EventLogger(configuration.subset("kafka"));
    }

    @Provides
    @Singleton
    ResponseLogger providesResponseLogger(EventLogger eventLogger) {
        return new ResponseLogger(eventLogger);
    }

    @Provides
    @Singleton
    V1EntryPoint providesV1EntryPoint(RequestDeserializer deserializer,
                                      ResponseSerializer serializer,
                                      AdFetcher adFetcher, ResponseLogger responseLogger) {
        return new V1EntryPoint(deserializer, serializer, adFetcher, responseLogger);
    }
}
