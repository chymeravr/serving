package com.chymeravr.rqhandler.entities.v1;

import lombok.Data;

/**
 * Created by rubbal on 17/1/17.
 */
@Data
public class AdRequest {
    private final String placementId;
    private final String appId;
    private final int hmdId;
}
