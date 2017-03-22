package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.server.servlets.AdservingServlet;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.HashMap;

/**
 * Created by rubbal on 21/3/17.
 */
public class AppServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(AdservingServlet.class).in(Singleton.class);

        bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

        HashMap<String, String> options = new HashMap<>();
        options.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

        serve("/*").with(GuiceContainer.class, options);
    }
}
