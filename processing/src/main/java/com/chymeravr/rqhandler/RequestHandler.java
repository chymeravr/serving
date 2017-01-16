package com.chymeravr.rqhandler;

import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.placement.PlacementCache;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RequestHandler extends AbstractHandler {

    private final AdgroupCache adgroupCache;
    private final PlacementCache placementCache;

    public RequestHandler(AdgroupCache adgroupCache, PlacementCache placementCache) {
        this.adgroupCache = adgroupCache;
        this.placementCache = placementCache;
    }


    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        out.write("yasdfsdaaa");
        out.flush();
        baseRequest.setHandled(true);
    }
}