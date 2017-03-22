package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.server.workers.RequestParser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rubbal on 21/3/17.
 */
@RequestScoped
public class RequestParserProvider implements Provider<RequestParser> {
    private HttpServletRequest request;

    @Inject
    public RequestParserProvider(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public RequestParser get() {
        return new RequestParser(request);
    }
}
