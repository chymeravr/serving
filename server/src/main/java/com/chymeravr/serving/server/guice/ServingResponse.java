package com.chymeravr.serving.server.guice;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import lombok.Data;

import java.io.IOException;

/**
 * Created by rubbal on 21/3/17.
 */
@RequestScoped
@Data
public class ServingResponse {
    private final AdservingServlet.RequestObject message;

    @Inject
    ServingResponse(RequestParser requestParser) throws IOException {
        this.message = requestParser.parse();
    }
}
