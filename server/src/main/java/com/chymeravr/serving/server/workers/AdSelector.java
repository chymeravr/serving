package com.chymeravr.serving.server.workers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubbal on 21/3/17.
 */
public class AdSelector {
    public List selectAds(RequestValidator.ValidatedRequest validatedRequest) {
        return new ArrayList<String>() {{
            add(validatedRequest.toString());
        }};
    }
}
