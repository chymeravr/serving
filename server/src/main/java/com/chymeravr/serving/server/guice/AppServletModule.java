package com.chymeravr.serving.server.guice;

import com.chymeravr.schemas.serving.AdMeta;
import com.chymeravr.schemas.serving.ServingResponse;
import com.chymeravr.serving.logging.EventLogger;
import com.chymeravr.serving.server.servlet.AdservingServlet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.HashMap;

/**
 * Created by rubbal on 21/3/17.
 */
public class AppServletModule extends JerseyServletModule {
    @Override
    protected void configureServlets() {
        bind(AdservingServlet.class).asEagerSingleton();

        bind(Key.get(MetricsServlet.class, Names.named("appMetrics"))).
                toProvider(getMetricsServletProvider()).in(Singleton.class);
        bind(Key.get(MetricsServlet.class, Names.named("kafkaMetrics"))).
                toProvider(getKafkaMetricsServletProvider()).in(Singleton.class);

        bind(JacksonJsonProvider.class).toProvider(getReaderProvider()).in(Singleton.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
        bind(MessageBodyReader.class).to(JacksonJsonProvider.class);

        HashMap<String, String> options = new HashMap<>();
        options.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

        serve("/health/metrics").with(Key.get(MetricsServlet.class, Names.named("appMetrics")));
        serve("/health/kafka").with(Key.get(MetricsServlet.class, Names.named("kafkaMetrics")));
        serve("/api/*").with(GuiceContainer.class, options);
    }

    private Provider<MetricsServlet> getMetricsServletProvider() {
        return new Provider<MetricsServlet>() {
            @Inject
            MetricRegistry metricsRegistry;

            @Override
            public MetricsServlet get() {
                return new MetricsServlet(metricsRegistry);
            }
        };
    }

    private Provider<MetricsServlet> getKafkaMetricsServletProvider() {
        return () -> new MetricsServlet(SharedMetricRegistries.getOrCreate(EventLogger.KAFKA_REGISTRY));
    }

    private Provider<JacksonJsonProvider> getReaderProvider() {
        return () -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SerializationConfig serConfig = objectMapper.getSerializationConfig();
            serConfig.addMixInAnnotations(ServingResponse.class, JacksonMixins.ResponseMixin.class);
            serConfig.addMixInAnnotations(AdMeta.class, JacksonMixins.AdMetaMixin.class);
            return new JacksonJsonProvider(objectMapper);
        };
    }
}
