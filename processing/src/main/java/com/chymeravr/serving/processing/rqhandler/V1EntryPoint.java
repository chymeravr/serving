package com.chymeravr.serving.processing.rqhandler;

import com.chymeravr.serving.logging.ResponseLogger;
import com.chymeravr.serving.processing.adfetcher.AdFetcher;
import com.chymeravr.serving.processing.rqhandler.serdes.RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.serdes.ResponseSerializer;
import com.codahale.metrics.MetricRegistry;

import javax.servlet.http.HttpServletResponse;

public class V1EntryPoint extends EntryPoint {


    public V1EntryPoint(RequestDeserializer deserializer,
                        ResponseSerializer serializer,
                        AdFetcher adFetcher,
                        ResponseLogger responseLogger,
                        String kafkaTopicName,
                        MetricRegistry metricRegistry) {
        super(deserializer, serializer, adFetcher, responseLogger, kafkaTopicName, metricRegistry);
    }

    public void setReponseHeaders(HttpServletResponse response) {
        response.setContentType("application/json; charset=utf-8");
    }
}