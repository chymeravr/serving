package com.chymeravr.serving.server.servlets;

import com.chymeravr.serving.server.ServingResponse;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by rubbal on 21/3/17.
 */
@Path("/api/v1/ads")
public class AdservingServlet {
    @Context
    ServletContext context;

    private final Provider<ServingResponse> servingResponseProvider;

    @Inject
    public AdservingServlet(Provider<ServingResponse> servingResponseProvider) {
        this.servingResponseProvider = servingResponseProvider;
    }

    @Data
    @NoArgsConstructor
    public static class RequestObject {
        private String key;
        private int value;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String echo(@QueryParam("text") String text) {
        return text;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ServingResponse twoFold(RequestObject requestObject) {
        context.setAttribute("request", requestObject);
        return servingResponseProvider.get();
    }
}
