package com.chymeravr.rqhandler;

import com.chymeravr.ad.AdCache;
import com.chymeravr.ad.AdEntity;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.adgroup.AdgroupEntity;
import com.chymeravr.placement.PlacementCache;
import com.chymeravr.rqhandler.entities.v1.AdRequest;
import com.chymeravr.rqhandler.entities.v1.AdResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@RequiredArgsConstructor
public class EntryPoint extends AbstractHandler {

    private final AdgroupCache adgroupCache;
    private final PlacementCache placementCache;
    private final AdCache adCache;
    public static final String CREATIVE_URL_PREFIX = "https://chymcreative.blob.core.windows.net/creatives/";

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {


        AdRequest adRequest = parseRequest(request);

        int hmdId = adRequest.getHmdId();
        Set<AdgroupEntity> adgroupsForHmd = adgroupCache.getAdgroupsForHmd(hmdId);
        Optional<AdgroupEntity> max = adgroupsForHmd.stream().max(Comparator.comparingDouble(AdgroupEntity::getBid));

        AdResponse adResponse = null;
        if (max.isPresent()) {
            Set<AdEntity> adsForAdgroup = adCache.getAdsForAdgroup(max.get().getId());
            if (adsForAdgroup.size() > 0) {
                ArrayList<AdEntity> adEntities = new ArrayList<>(adsForAdgroup);
                Collections.shuffle(adEntities);
                adResponse = new AdResponse(CREATIVE_URL_PREFIX + adEntities.get(0).getUrl());
            }
        }

        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        if (adResponse != null) {
            out.write(new Gson().toJson(adResponse));
        }
        out.flush();
        baseRequest.setHandled(true);
    }

    private AdRequest parseRequest(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        return new Gson().fromJson(data, AdRequest.class);
    }
}