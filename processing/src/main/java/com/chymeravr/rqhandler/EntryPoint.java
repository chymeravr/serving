package com.chymeravr.rqhandler;

import com.chymeravr.ad.AdCache;
import com.chymeravr.ad.AdEntity;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.adgroup.AdgroupEntity;
import com.chymeravr.placement.PlacementCache;
import com.chymeravr.rqhandler.entities.v1.request.AdRequest;
import com.chymeravr.rqhandler.entities.v1.request.RequestObjects;
import com.chymeravr.rqhandler.entities.v1.response.AdResponse;
import com.chymeravr.rqhandler.entities.v1.response.ResponseObjects;
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
    private static final String CREATIVE_URL_PREFIX = "https://chymcreative.blob.core.windows.net/creatives/";

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
        final UUID requestId = UUID.randomUUID();

        AdRequest adRequest = parseRequest(request);
        AdResponse adResponse = getAdResponse(adRequest);

        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        if (adResponse != null) {
            out.write(new Gson().toJson(adResponse));
        }
        out.flush();
        baseRequest.setHandled(true);
    }

    private AdResponse getAdResponse(AdRequest adRequest) {
        List<RequestObjects.Placement> placements = adRequest.getPlacements();
        int hmdId = adRequest.getHmdId();
        ArrayList<AdgroupEntity> adgroupsForHmd = new ArrayList<>(adgroupCache.getAdgroupsForHmd(hmdId));

        adgroupsForHmd.sort(Comparator.comparingDouble(x -> -x.getBid())); // reverse sort

        List<ResponseObjects.AdMeta> topAds = getTopAds(adgroupsForHmd, placements.size());
        // Assign ads to a placement
        Map<String, ResponseObjects.AdMeta> adsMap = new HashMap<>();
        for (int i = 0; i < topAds.size(); i++) { // At most as many top Ads as placements
            adsMap.put(placements.get(i).getId(), topAds.get(i));
        }
        return new AdResponse(200, "OK", -1, adsMap);
    }

    private List<ResponseObjects.AdMeta> getTopAds(ArrayList<AdgroupEntity> adgroupsForHmd, int adsToSelect) {
        List<ResponseObjects.AdMeta> ads = new ArrayList<>();
        int adsSelected = 0;

        for (AdgroupEntity adgroupEntity : adgroupsForHmd) {
            Set<AdEntity> adsForAdgroup = adCache.getAdsForAdgroup(adgroupEntity.getId());
            for (AdEntity ad : adsForAdgroup) {
                ads.add(new ResponseObjects.AdMeta(UUID.randomUUID().toString(), CREATIVE_URL_PREFIX + ad.getUrl()));
                adsSelected++;
                if (adsSelected == adsToSelect) {
                    return ads;
                }
            }
        }
        return ads;
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