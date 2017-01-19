package com.chymeravr.rqhandler;

import com.chymeravr.adfetcher.AdFetcher;
import com.chymeravr.logger.ResponseLogger;
import com.chymeravr.rqhandler.iface.EntryPoint;
import com.chymeravr.rqhandler.iface.RequestDeserializer;
import com.chymeravr.rqhandler.iface.ResponseSerializer;

import javax.servlet.http.HttpServletResponse;

public class V1EntryPoint extends EntryPoint {


    public V1EntryPoint(RequestDeserializer deserializer, ResponseSerializer serializer, AdFetcher adFetcher,
                        ResponseLogger responseLogger) {
        super(deserializer, serializer, adFetcher, responseLogger);
    }

    public void setReponseHeaders(HttpServletResponse response) {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}