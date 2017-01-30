package com.chymeravr.serving.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by rubbal on 30/1/17.
 */
public class ConfigModule extends AbstractModule {

    private final String filePath;

    public ConfigModule(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    Configuration providesConfiguration() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration(filePath);
        config.setThrowExceptionOnMissing(true);
        return config;
    }
}
