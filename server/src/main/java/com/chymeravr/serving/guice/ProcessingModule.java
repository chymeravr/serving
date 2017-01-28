package com.chymeravr.serving.guice;

import com.chymeravr.serving.ad.AdCache;
import com.chymeravr.serving.adfetcher.AdFetcher;
import com.chymeravr.serving.adgroup.AdgroupCache;
import com.chymeravr.serving.kafka.EventLogger;
import com.chymeravr.serving.logger.ResponseLogger;
import com.chymeravr.serving.rqhandler.V1EntryPoint;
import com.chymeravr.serving.rqhandler.iface.RequestDeserializer;
import com.chymeravr.serving.rqhandler.iface.ResponseSerializer;
import com.chymeravr.serving.rqhandler.entities.v1.json.V1RequestDeserializer;
import com.chymeravr.serving.rqhandler.entities.v1.json.V1ResponseSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by rubbal on 19/1/17.
 */
public class ProcessingModule extends AbstractModule {
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
    // TODO : Add properties from file
    EventLogger providesEventLogger() {
        return new EventLogger(null);
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
