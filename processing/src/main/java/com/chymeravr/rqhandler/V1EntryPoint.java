package com.chymeravr.rqhandler;

import com.chymeravr.adfetcher.AdFetcher;
import com.chymeravr.rqhandler.entities.iface.EntryPoint;
import com.chymeravr.rqhandler.entities.iface.RequestDeserializer;
import com.chymeravr.rqhandler.entities.iface.ResponseSerializer;

import javax.servlet.http.HttpServletResponse;

public class V1EntryPoint extends EntryPoint {


    public V1EntryPoint(RequestDeserializer deserializer, ResponseSerializer serializer, AdFetcher adFetcher) {
        super(deserializer, serializer, adFetcher);
    }

    public void setReponseHeaders(HttpServletResponse response) {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}