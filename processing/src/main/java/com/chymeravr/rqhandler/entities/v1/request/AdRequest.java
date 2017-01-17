package com.chymeravr.rqhandler.entities.v1.request;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class AdRequest {
    private final long timestamp;
    private final String appId;
    private final List<RequestObjects.Placement> placements;
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
