package com.chymeravr.rqhandler.entities.request;

import com.chymeravr.thrift.serving.AdFormat;
import lombok.Data;

/**
 * Created by rubbal on 17/1/17.
 */
public class RequestObjects {
    @Data
    public static class Demographic {
        private final String dob;
        private final String gender;
        private final String email;
    }

    @Data
    public static class Location {
        private final double lat;
        private final double lon;
        private final double accuracy;
    }

    @Data
    public static class DeviceInfo {
        private final String manufacturer;
        private final String model;
        private final String ram;
    }
}
