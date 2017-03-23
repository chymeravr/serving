package com.chymeravr.serving.workers.validator;

import com.chymeravr.schemas.serving.ErrorCode;
import com.chymeravr.schemas.serving.ServingRequest;
import lombok.Data;

/**
 * Created by rubbal on 23/3/17.
 */
@Data
public class ValidatedRequest {
    private final ServingRequest request;
    private final String requestId;
    private final boolean isValid;
    private final ErrorCode errorCode;
}
