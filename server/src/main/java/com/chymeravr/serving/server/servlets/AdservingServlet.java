package com.chymeravr.serving.server.servlets;


import com.chymeravr.schemas.serving.ServingRequest;
import com.chymeravr.schemas.serving.ServingResponse;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by rubbal on 21/3/17.
 */
@Path("/v1/ads")
public class AdservingServlet {
    @Inject
    private Provider<ServingResponse> servingResponseProvider;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ServingResponse echo(ServingRequest servingRequest) throws IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            MDC.put("chym_trace", request.getHeader("chym_trace"));
            request.setAttribute("request", servingRequest);
            request.setAttribute("requestId", requestId);

            return servingResponseProvider.get();
        } finally {
            MDC.clear();
        }
    }
}
