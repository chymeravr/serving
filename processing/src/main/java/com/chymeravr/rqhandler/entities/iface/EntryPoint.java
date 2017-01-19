package com.chymeravr.rqhandler.entities.iface;

import com.chymeravr.adfetcher.AdFetcher;
import com.chymeravr.rqhandler.entities.request.Request;
import com.chymeravr.rqhandler.entities.response.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by rubbal on 19/1/17.
 */

@RequiredArgsConstructor
public abstract class EntryPoint extends AbstractHandler {

    private final RequestDeserializer deserializer;
    private final ResponseSerializer serializer;
    private final AdFetcher adFetcher;

    public void handle(String target,
                       org.eclipse.jetty.server.Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
        final UUID requestId = UUID.randomUUID();

        Request adRequest = deserializer.deserializeRequest(request);
        Response adResponse = adFetcher.getAdResponse(adRequest);

        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        if (adResponse != null) {
            out.write(new String(serializer.serialize(adResponse)));
        }
        out.flush();
        baseRequest.setHandled(true);
    }
}