package com.chymeravr.serving.server.servlets;


import lombok.Data;
import lombok.NoArgsConstructor;

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

/**
 * Created by rubbal on 21/3/17.
 */
@Path("/api/v1/ads")
public class AdservingServlet {
    @Inject
    private Provider<RequestObject> requestObjectProvider;

    @Data
    @NoArgsConstructor
    public static class RequestObject {
        private int id;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RequestObject echo(RequestObject requestObject, @Context HttpServletRequest httpRequest
    ) throws IOException {
        httpRequest.setAttribute("request", requestObject);
        System.out.println(requestObjectProvider.get() + " |" + requestObject);
        return requestObjectProvider.get();
    }
}
