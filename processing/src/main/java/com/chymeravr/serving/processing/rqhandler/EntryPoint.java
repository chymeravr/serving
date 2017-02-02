package com.chymeravr.serving.processing.rqhandler;

import com.chymeravr.schemas.serving.ImpressionInfo;
import com.chymeravr.schemas.serving.RequestInfo;
import com.chymeravr.schemas.serving.ResponseCode;
import com.chymeravr.schemas.serving.ServingLog;
import com.chymeravr.serving.logging.ResponseLogger;
import com.chymeravr.serving.processing.adfetcher.AdFetcher;
import com.chymeravr.serving.processing.rqhandler.entities.request.Request;
import com.chymeravr.serving.processing.rqhandler.entities.response.AdResponse;
import com.chymeravr.serving.processing.rqhandler.entities.response.InternalAdResponse;
import com.chymeravr.serving.processing.rqhandler.iface.RequestDeserializer;
import com.chymeravr.serving.processing.rqhandler.iface.ResponseSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TSerializer;
import org.eclipse.jetty.server.handler.AbstractHandler;

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

@RequiredArgsConstructor
@Slf4j
public abstract class EntryPoint extends AbstractHandler {

    private final RequestDeserializer deserializer;
    private final ResponseSerializer serializer;
    private final AdFetcher adFetcher;
    private final ResponseLogger responseLogger;
    private final String downStreamTopicName;

    public void handle(String target,
                       org.eclipse.jetty.server.Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        final UUID requestId = UUID.randomUUID();
        List<Integer> experiments = new ArrayList<>();
        Request adRequest = deserializer.deserializeRequest(request);
        InternalAdResponse internalAdResponse = adFetcher.getAdResponse(adRequest, experiments);
        AdResponse adResponse = new AdResponse(internalAdResponse);
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
                adRequest.getDeviceInfo().getManufacturer());


        internalAdResponse.getAds().entrySet().forEach(placementImpression -> {
            String placementId = placementImpression.getKey();
            ImpressionInfo impressionInfo = placementImpression.getValue();
            ServingLog servingLog = new ServingLog(
                    System.currentTimeMillis(),
                    requestId.toString(),
                    adRequest.getSdkVersion(),
                    experiments,
                    requestInfo,
                    ResponseCode.SERVED,
                    placementId,
                    impressionInfo);
            try {
                responseLogger.sendMessage(impressionInfo.getServingId(),
                        encode(new TSerializer().serialize(servingLog)),
                        downStreamTopicName);
            } catch (Exception e) {
                log.error("Unable to send kafka message");
            }
        });
    }

    private String encode(byte[] binaryData) {
        return Base64.getEncoder().encodeToString(binaryData);
    }

    protected abstract void setReponseHeaders(HttpServletResponse response);
}