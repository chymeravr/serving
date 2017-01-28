package com.chymeravr.serving.ad;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by rubbal on 17/1/17.
 */

@ToString
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AdEntity {
    private final String id;
    private final String adgroupId;
    private final String url;
}
