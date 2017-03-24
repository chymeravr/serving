package com.chymeravr.serving.server.servlet;


import com.chymeravr.schemas.serving.ServingRequest;
import com.chymeravr.schemas.serving.ServingResponse;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.UUID;


@Path("/v1/ads")
public class AdservingServlet {
    @Inject
    private Provider<ServingResponse> servingResponseProvider;

    @Inject
    @Named("LatencyTimer")
    private Timer timer;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public ServingResponse echo(ServingRequest servingRequest) throws IOException {
        Timer.Context time = timer.time();
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            MDC.put("chym_trace", request.getHeader("chym_trace"));
            request.setAttribute("request", servingRequest);
            request.setAttribute("requestId", requestId);

            return servingResponseProvider.get();
        } finally {
            MDC.clear();
            time.stop();
        }
    }
}
