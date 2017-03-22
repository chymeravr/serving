package com.chymeravr.serving.server.dag;

import com.chymeravr.serving.server.ServingResponse;
import com.chymeravr.serving.server.guice.RequestParserProvider;
import com.chymeravr.serving.server.guice.ServingResponseProvider;
import com.chymeravr.serving.server.servlets.AdservingServlet;
import com.chymeravr.serving.server.workers.AdSelector;
import com.chymeravr.serving.server.workers.RequestParser;
import com.chymeravr.serving.server.workers.RequestValidator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletScopes;

import java.io.IOException;
import java.util.List;

/**
 * Created by rubbal on 21/3/17.
 */
public class ExecutionDag extends AbstractModule {
    @Override
    protected void configure() {
        bind(ServingResponse.class).toProvider(ServingResponseProvider.class).in(ServletScopes.REQUEST);
        bind(AdservingServlet.RequestObject.class).toProvider(RequestParserProvider.class).in(ServletScopes.REQUEST);
    }

    @Provides
    @RequestScoped
    RequestValidator.ValidatedRequest providesValidatedRequest(RequestValidator requestValidator,
                                                               RequestParser requestParser) throws IOException {
        return requestValidator.validatedRequest(requestParser.parse());
    }

    @Provides
    @RequestScoped
    List providesSelectedAds(AdSelector adSelector, RequestValidator.ValidatedRequest validatedRequest) {
        return adSelector.selectAds(validatedRequest);
    }
}
