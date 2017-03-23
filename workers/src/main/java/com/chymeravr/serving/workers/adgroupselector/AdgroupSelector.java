package com.chymeravr.serving.workers.adgroupselector;

import com.chymeravr.serving.cache.adgroup.AdgroupCache;
import com.chymeravr.serving.entities.cache.AdgroupEntity;
import com.chymeravr.serving.workers.validator.ValidatedRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rubbal on 23/3/17.
 */
@Data
@Slf4j
public class AdgroupSelector {
    private final AdgroupCache adgroupCache;
    private final double defaultCtr;

    public List<AdgroupEntity> getValidAdgroups(ValidatedRequest request) {
        int hmdId = request.getRequest().getHmdId();
        ArrayList<AdgroupEntity> adgroupsForHmd = new ArrayList<>(adgroupCache.getAdgroupsForHmd(hmdId));
        log.info("Candidate adgroups for the HMD: {}", adgroupsForHmd);

        List<AdgroupEntity> adgroupsWithBudget = adgroupsForHmd.stream().filter(x ->
                x.getTodayBurn() < x.getDailyBudget() &&
                        x.getTotalBurn() < x.getTotalBudget() &&
                        x.getCmpTodayBurn() < x.getCmpDailyBudget() &&
                        x.getCmpTotalBurn() < x.getCmpTotalBudget()

        ).collect(Collectors.toList());
        log.info("Candidates having budget: {}", adgroupsWithBudget);

        return adgroupsWithBudget;
    }
}

