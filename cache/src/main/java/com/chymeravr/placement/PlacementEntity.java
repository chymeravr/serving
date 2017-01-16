package com.chymeravr.placement;

import com.chymeravr.enums.AppStore;
import lombok.Data;

/**
 * Created by rubbal on 16/1/17.
 */
@Data
public class PlacementEntity {
    private final String id;
    private final String appId;
    private final AppStore appStore;
}
