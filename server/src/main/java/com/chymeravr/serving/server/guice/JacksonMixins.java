package com.chymeravr.serving.server.guice;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by rubbal on 23/3/17.
 */
public class JacksonMixins {


    interface ResponseMixin {
        @JsonIgnore
        boolean isSetRequestId();

        @JsonIgnore
        boolean isSetResponseCode();

        @JsonIgnore
        boolean isSetAds();

        @JsonIgnore
        boolean isSetErrorCode();

        @JsonIgnore
        boolean isSetAdType();

        @JsonIgnore
        boolean getAdsSize();
    }

    interface AdMetaMixin {
        @JsonIgnore
        boolean isSetServingId();

        @JsonIgnore
        boolean isSetMediaUrl();

        @JsonIgnore
        boolean isSetClickUrl();

        @JsonIgnore
        boolean isSetAppName();
    }
}
