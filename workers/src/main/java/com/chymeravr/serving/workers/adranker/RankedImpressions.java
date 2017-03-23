package com.chymeravr.serving.workers.adranker;

import com.chymeravr.serving.entities.Impression;
import lombok.Data;

import java.util.Map;

/**
 * Created by rubbal on 23/3/17.
 */
@Data
public class RankedImpressions {
    private final Map<String, Impression> adsMap;
}
