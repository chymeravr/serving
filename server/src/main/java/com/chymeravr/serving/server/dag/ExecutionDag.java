package com.chymeravr.serving.server.dag;

import com.chymeravr.schemas.serving.ResponseCode;
import com.chymeravr.schemas.serving.ServingResponse;
import com.chymeravr.serving.entities.Impression;
import com.chymeravr.serving.entities.InternalAdResponse;
import com.chymeravr.serving.entities.cache.AdgroupEntity;
import com.chymeravr.serving.workers.adgroupselector.AdgroupSelector;
import com.chymeravr.serving.workers.adranker.AdRanker;
import com.chymeravr.serving.workers.adranker.RankedImpressions;
import com.chymeravr.serving.workers.adselector.AdSelector;
import com.chymeravr.serving.workers.responsewriter.ResponseWriter;
import com.chymeravr.serving.workers.validator.RequestValidator;
import com.chymeravr.serving.workers.validator.ValidatedRequest;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletScopes;
import org.apache.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rubbal on 21/3/17.
 */
public class ExecutionDag extends AbstractModule {
    @Override
    protected void configure() {
        bind(RankedImpressions.class).toProvider(ImpressionMapProvider.class).in(ServletScopes.REQUEST);
        bind(ServingResponse.class).toProvider(ResponseProvider.class).in(ServletScopes.REQUEST);
    }

    @Provides
    @RequestScoped
    ValidatedRequest providesValidatedRequest(RequestValidator requestValidator,
                                              HttpServletRequest httpServletRequest) throws IOException {
        return requestValidator.validate(httpServletRequest);
    }

    @Provides
    @RequestScoped
    @Named(WorkerOutputNames.SELECTED_ADGROUPS)
    List<AdgroupEntity> providesSelectedAdgroups(AdgroupSelector adgroupSelector, ValidatedRequest validatedRequest) {
        return adgroupSelector.getValidAdgroups(validatedRequest);
    }

    @Provides
    @RequestScoped
    @Named(WorkerOutputNames.SELECTED_ADS)
    List<Impression> providesSelectedAds(AdSelector adSelector,
                                         @Named(WorkerOutputNames.SELECTED_ADGROUPS) List<AdgroupEntity> adgroups) {
        return adSelector.getValidAds(adgroups);
    }


    @Provides
    @RequestScoped
    InternalAdResponse providesInternalAdResponse(ValidatedRequest validatedRequest,
                                                  Provider<RankedImpressions> impressionMapProviderProvider) {
        if (validatedRequest.isValid()) {
            Map<String, Impression> impressionMap = impressionMapProviderProvider.get().getAdsMap();
            return new InternalAdResponse(impressionMap.size() > 0 ? ResponseCode.SERVED : ResponseCode.NO_AD,
                    HttpStatus.SC_OK,
                    new ArrayList<>(),
                    impressionMap
            );
        } else {
            return new InternalAdResponse(ResponseCode.BAD_REQUEST,
                    HttpStatus.SC_BAD_REQUEST, new ArrayList<>(),
                    new HashMap<>()
            );
        }
    }

    @RequestScoped
    public static class ImpressionMapProvider implements Provider<RankedImpressions> {
        private final AdRanker adRanker;
        private final ValidatedRequest validatedRequest;
        private final List<Impression> ads;

        @Inject
        public ImpressionMapProvider(AdRanker adRanker,
                                     ValidatedRequest validatedRequest,
                                     @Named(WorkerOutputNames.SELECTED_ADS) List<Impression> ads) {
            this.adRanker = adRanker;
            this.validatedRequest = validatedRequest;
            this.ads = ads;
        }

        @Override
        @Named("ImpressionMap")
        public RankedImpressions get() {
            return adRanker.rankAds(ads, validatedRequest);
        }
    }

    @RequestScoped
    public static class ResponseProvider implements Provider<ServingResponse> {
        private final ResponseWriter responseWriter;
        private final InternalAdResponse internalAdResponse;
        private final ValidatedRequest validatedRequest;

        @Inject
        public ResponseProvider(ResponseWriter responseWriter,
                                InternalAdResponse internalAdResponse,
                                ValidatedRequest validatedRequest) {
            this.responseWriter = responseWriter;
            this.internalAdResponse = internalAdResponse;
            this.validatedRequest = validatedRequest;
        }

        @Override
        public ServingResponse get() {
            ServingResponse response = responseWriter.getResponse(internalAdResponse, validatedRequest);
            if (validatedRequest.getErrorCode() != null) {
                response.setErrorCode(validatedRequest.getErrorCode());
            }
            return response;
        }
    }
}


