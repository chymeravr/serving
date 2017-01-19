package com.chymeravr.rqhandler.entities.request;

import com.chymeravr.thrift.serving.Placement;
import lombok.Data;

import java.util.List;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class Request {
    private final long timestamp;
    private final int sdkVersion;
    private final String appId;
    private final List<Placement> placements;
    private final String osId;
    private final String osVersion;
    private final String userId;
    private final int hmdId;
    private final RequestObjects.Location location;
    private final RequestObjects.Demographic demographics;
    private final RequestObjects.DeviceInfo deviceInfo;
    private final String connectivity;
    private final String wifiName;
}
