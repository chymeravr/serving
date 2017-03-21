package com.chymeravr.serving.server.guice;

import com.google.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by rubbal on 21/3/17.
 */
public class RequestParser {
    HttpServletRequest request;

    @Inject
    public RequestParser(HttpServletRequest request) {
        this.request = request;
    }

    public AdservingServlet.RequestObject parse() throws IOException {
        return (AdservingServlet.RequestObject) request.getServletContext().getAttribute("request");
    }
}
