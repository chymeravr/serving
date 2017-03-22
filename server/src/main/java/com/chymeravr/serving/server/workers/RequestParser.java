package com.chymeravr.serving.server.workers;

import com.chymeravr.serving.server.servlets.AdservingServlet;
import com.google.inject.Inject;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by rubbal on 21/3/17.
 */

@Data
public class RequestParser {

    private final HttpServletRequest request;

    @Inject
    public RequestParser(HttpServletRequest request) {
        this.request = request;
    }

    public AdservingServlet.RequestObject parse() throws IOException {
        return (AdservingServlet.RequestObject) request.getServletContext().getAttribute("request");
    }
}
