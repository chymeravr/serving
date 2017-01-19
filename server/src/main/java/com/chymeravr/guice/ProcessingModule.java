package com.chymeravr.guice;

import com.chymeravr.ad.AdCache;
import com.chymeravr.adfetcher.AdFetcher;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.kafka.EventLogger;
import com.chymeravr.logger.ResponseLogger;
import com.chymeravr.rqhandler.V1EntryPoint;
import com.chymeravr.rqhandler.iface.RequestDeserializer;
import com.chymeravr.rqhandler.iface.ResponseSerializer;
import com.chymeravr.rqhandler.entities.v1.json.V1RequestDeserializer;
import com.chymeravr.rqhandler.entities.v1.json.V1ResponseSerializer;
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
    EventLogger providesEventLogger() {
        return new EventLogger();
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
