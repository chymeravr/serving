package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.server.servlets.AdservingServlet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rubbal on 21/3/17.
 */
@RequestScoped
public class RequestParserProvider implements Provider<AdservingServlet.RequestObject> {
    @Inject
    private HttpServletRequest request;

    @Override
    public AdservingServlet.RequestObject get() {
        return (AdservingServlet.RequestObject) request.getAttribute("request");
    }
}
