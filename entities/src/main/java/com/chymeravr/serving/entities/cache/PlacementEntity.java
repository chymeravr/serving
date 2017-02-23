package com.chymeravr.serving.entities.cache;

import com.chymeravr.serving.enums.AppStore;
import com.googlecode.cqengine.attribute.Attribute;
import lombok.Data;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

/**
 * Created by rubbal on 16/1/17.
 */
@Data
public class PlacementEntity implements AbstractEntity {
    public static final Attribute<PlacementEntity, String> ID = attribute(PlacementEntity::getId);

    private final String id;
    private final String appId;
    private final AppStore appStore;
}
