package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.server.workers.AdSelector;
import com.chymeravr.serving.server.workers.RequestValidator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by rubbal on 21/3/17.
 */
public class WorkerModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    RequestValidator providesRequestValidator() {
        return new RequestValidator();
    }

    @Provides
    @Singleton
    AdSelector providesAdSelector() {
        return new AdSelector();
    }
}
