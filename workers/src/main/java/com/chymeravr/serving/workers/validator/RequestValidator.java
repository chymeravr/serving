package com.chymeravr.serving.workers.validator;

import com.chymeravr.schemas.serving.ErrorCode;
import com.chymeravr.schemas.serving.Placement;
import com.chymeravr.schemas.serving.ServingRequest;
import com.chymeravr.serving.cache.placement.PlacementCache;
import com.chymeravr.serving.entities.cache.PlacementEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.googlecode.cqengine.query.QueryFactory.equal;

/**
 * Created by rubbal on 23/3/17.
 */
@Data
@Slf4j
public class RequestValidator {
    private final PlacementCache placementCache;

    public ValidatedRequest validate(HttpServletRequest httpServletRequest) {
        ServingRequest request = (ServingRequest) httpServletRequest.getAttribute("request");
        String requestId = (String) httpServletRequest.getAttribute("requestId");

        List<Placement> placements = request.getPlacements();
        String appId = request.getAppId();

        boolean validPlacement = true;
        boolean validAppId = true;

        for (Placement placement : placements) {
            PlacementEntity placementEntity = placementCache.queryEntity(equal(PlacementEntity.ID, placement.getId()));
            if (placementEntity == null) {
                validPlacement = false;
                break;
            }
            if (!placementEntity.getAppId().equals(appId)) {
                validAppId = false;
                break;
            }
        }

        log.debug("Valid App Id : {}, Valid Placement Id : {}", validAppId, validPlacement);

        if (validAppId && validPlacement) {
            return new ValidatedRequest(request, requestId, true, null);
        } else if (!validAppId) {
            return new ValidatedRequest(request, requestId, false, ErrorCode.INACTIVE_APP);
        } else {
            return new ValidatedRequest(request, requestId, false, ErrorCode.APP_ID_NOT_SET);
        }
    }
}
