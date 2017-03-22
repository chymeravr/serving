package com.chymeravr.serving.server.workers;

import com.chymeravr.serving.server.servlets.AdservingServlet;
import lombok.Data;

/**
 * Created by rubbal on 21/3/17.
 */
public class RequestValidator {
    @Data
    public static class ValidatedRequest {
        private final AdservingServlet.RequestObject requestObject;
    }

    public ValidatedRequest validatedRequest(AdservingServlet.RequestObject requestObject) {
        return new ValidatedRequest(requestObject);
    }
}
