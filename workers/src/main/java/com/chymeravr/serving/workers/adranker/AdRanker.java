package com.chymeravr.serving.workers.adranker;

import com.chymeravr.schemas.serving.Placement;
import com.chymeravr.serving.entities.Impression;
import com.chymeravr.serving.workers.validator.ValidatedRequest;
import lombok.Data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by rubbal on 23/3/17.
 */
@Data
public class AdRanker {
    public RankedImpressions rankAds(List<Impression> impressions, ValidatedRequest validatedRequest) {
        int adsRequired = validatedRequest.getRequest().getPlacementsSize();
        List<Placement> placements = validatedRequest.getRequest().getPlacements();

        impressions.sort(Comparator.comparingDouble(x -> x.getCostPrice() - x.getSellingPrice())); // reverse sort
        List<Impression> topAds = impressions.stream().limit(adsRequired).collect(toList());
        Map<String, Impression> adsMap = new HashMap<>();

        for (int i = 0; i < topAds.size(); i++) {
            adsMap.put(placements.get(i).getId(), topAds.get(i));
        }

        return new RankedImpressions(adsMap);
    }
}
