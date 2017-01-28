package com.chymeravr.serving.processing.rqhandler;

import com.chymeravr.serving.processing.rqhandler.iface.RequestDeserializer;
import com.chymeravr.serving.processing.adfetcher.AdFetcher;
import com.chymeravr.serving.processing.logger.ResponseLogger;
import com.chymeravr.serving.processing.rqhandler.iface.EntryPoint;
import com.chymeravr.serving.processing.rqhandler.iface.ResponseSerializer;

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