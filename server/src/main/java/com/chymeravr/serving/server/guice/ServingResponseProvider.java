package com.chymeravr.serving.server.guice;

import com.chymeravr.serving.server.ServingResponse;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

import java.util.List;

/**
 * Created by rubbal on 21/3/17.
 */
@RequestScoped
public class ServingResponseProvider implements Provider<ServingResponse> {
    private List ads;

    @Inject
    public ServingResponseProvider(List ads) {
        this.ads = ads;
    }

    @Override
    public ServingResponse get() {
        return new ServingResponse(ads);
    }
}
