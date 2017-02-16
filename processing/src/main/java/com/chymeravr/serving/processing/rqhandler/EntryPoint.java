package com.chymeravr.serving.processing.rqhandler;

import com.chymeravr.schemas.serving.*;
import com.chymeravr.serving.logging.ResponseLogger;
import com.chymeravr.serving.processing.adfetcher.AdFetcher;
import com.chymeravr.serving.processing.rqhandler.entities.response.InternalAdResponse;
import com.chymeravr.serving.processing.rqhandler.serdes.RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.serdes.ResponseSerializer;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TSerializer;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.MDC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Created by rubbal on 19/1/17.
 */

@Slf4j
public abstract class EntryPoint extends AbstractHandler {

    private final RequestDeserializer deserializer;
    private final ResponseSerializer serializer;
    private final AdFetcher adFetcher;
    private final ResponseLogger responseLogger;
    private final String downStreamTopicName;
    private final Counter requestsReceived;
    private final Counter requestsResponded;

    protected EntryPoint(RequestDeserializer deserializer,
                         ResponseSerializer serializer,
                         AdFetcher adFetcher,
                         ResponseLogger responseLogger,
                         String downStreamTopicName,
                         MetricRegistry metricRegistry) {
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.adFetcher = adFetcher;
        this.responseLogger = responseLogger;
        this.downStreamTopicName = downStreamTopicName;

        requestsReceived = metricRegistry.counter("App.requestsReceived");
        requestsResponded = metricRegistry.counter("App.requestsResponded");
    }

    public void handle(String target,
                       org.eclipse.jetty.server.Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        final UUID requestId = UUID.randomUUID();
        MDC.put("requestId", requestId.toString());
        MDC.put("chym_trace", request.getHeader("chym_trace"));
        requestsReceived.inc();
        List<Integer> experiments = new ArrayList<>();

        ServingRequest adRequest = deserializer.deserializeRequest(request);
        log.info("Ad Request : {}", adRequest);
        log.error("Ad ERROR : {}", adRequest); // TODO
        InternalAdResponse internalAdResponse = adFetcher.getAdResponse(adRequest, experiments);
        ServingResponse adResponse = internalAdResponse.getServingResponse(requestId.toString());
        setReponseHeaders(response);
        PrintWriter out = response.getWriter();
        out.write(new String(serializer.serialize(adResponse)));
        out.flush();

        baseRequest.setHandled(true);

        RequestInfo requestInfo = new RequestInfo(
                adRequest.getAppId(),
                adRequest.getPlacements(),
                adRequest.getHmdId(),
                adRequest.getOsId(),
                adRequest.getOsVersion(),
                adRequest.getDevice().getManufacturer());

        ResponseLog servingLog = new ResponseLog(
                System.currentTimeMillis(),
                requestId.toString(),
                adRequest.getSdkVersion(),
                experiments,
                requestInfo,
                ResponseCode.SERVED,
                internalAdResponse.getAds());
        try {
            responseLogger.sendMessage(requestId.toString(),
                    encode(new TSerializer().serialize(servingLog)),
                    downStreamTopicName);
        } catch (Exception e) {
            log.error("Unable to send kafka message", e);
        }

//        MDC.remove("requestId");
        MDC.clear();
        requestsResponded.inc();
    }

    private String encode(byte[] binaryData) {
        return Base64.getEncoder().encodeToString(binaryData);
    }

    protected abstract void setReponseHeaders(HttpServletResponse response);
}