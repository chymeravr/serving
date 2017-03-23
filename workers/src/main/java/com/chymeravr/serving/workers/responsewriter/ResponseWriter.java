package com.chymeravr.serving.workers.responsewriter;

import com.chymeravr.schemas.serving.RequestInfo;
import com.chymeravr.schemas.serving.ResponseLog;
import com.chymeravr.schemas.serving.ServingRequest;
import com.chymeravr.schemas.serving.ServingResponse;
import com.chymeravr.serving.logging.ResponseLogger;
import com.chymeravr.serving.entities.InternalAdResponse;
import com.chymeravr.serving.workers.validator.ValidatedRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.ArrayList;
import java.util.Base64;

/**
 * Created by rubbal on 23/3/17.
 */
@Data
@Slf4j
public class ResponseWriter {
    private final ResponseLogger responseLogger;
    private final String downStreamTopicName;

    public ServingResponse getResponse(InternalAdResponse internalAdResponse,
                                       ValidatedRequest validatedRequest) {
        ServingRequest request = validatedRequest.getRequest();
        String requestId = validatedRequest.getRequestId();
        ServingResponse adResponse = internalAdResponse.getServingResponse(validatedRequest.getRequestId());

        RequestInfo requestInfo = new RequestInfo(
                request.getAppId(),
                request.getPlacements(),
                request.getHmdId(),
                request.getOsId(),
                request.getOsVersion(),
                request.getDevice().getManufacturer());

        ResponseLog servingLog = new ResponseLog(
                System.currentTimeMillis(),
                validatedRequest.getRequestId(),
                request.getSdkVersion(),
                new ArrayList<>(),
                requestInfo,
                internalAdResponse.getResponseCode(),
                internalAdResponse.getImpressionLogInfo());

        log.info("Kafka message to be sent: {} :: {}", requestId, servingLog);
        try {
            responseLogger.sendMessage(requestId,
                    encode(new TSerializer().serialize(servingLog)),
                    downStreamTopicName);
        } catch (TException e) {
            log.error("Unable to send Kafka message", e);
        }

        return adResponse;

    }

    private String encode(byte[] binaryData) {
        return Base64.getEncoder().encodeToString(binaryData);
    }

}
