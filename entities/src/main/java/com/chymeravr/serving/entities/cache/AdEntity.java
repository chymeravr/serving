package com.chymeravr.serving.entities.cache;

import com.googlecode.cqengine.attribute.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

/**
 * Created by rubbal on 17/1/17.
 */

@ToString
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AdEntity {
    public static final Attribute<AdEntity, String> ID = attribute(AdEntity::getId);
    public static final Attribute<AdEntity, String> ADGROUP_ID = attribute(AdEntity::getAdgroupId);

    private final String id;
    private final String adgroupId;
    private final String url;
    private final String landingPage;
    private final int adType;
}
