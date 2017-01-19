namespace java com.chymeravr.thrift.serving

enum ResponseCode {
    SERVED,
    NO_AD,
    BAD_REQUEST
}

enum ErrorCode {
    APP_ID_NOT_SET,
    AD_FORMAT_NOT_SET,
    INACTIVE_APP,
    HMD_NOT_SET,
}

enum AdFormat {
    IMG_360,
    VID_360
}

struct Placement {
    1: required string id;
    2: required AdFormat adFormat;
}

struct RequestInfo {
    1: required string appId;
    2: required list<Placement> placementIds;
    3: required i32 HmdId;
    4: required string OsId;
    5: required string OsVersion;
    6: required string deviceId;
}

struct ImpressionInfo {
    1: required string servingId;
    2: required string advertiserId;
    3: required string adgroupId;
    4: required string adId;
    5: required double costPrice;
    6: required double sellingPrice;
    7: required string creativeUrl;
}

struct ServingLog {
    1: required string requestId;
    2: required i32 sdkVersion;
    3: required list<i32> experimentIds;
    4: required RequestInfo requestInfo;
    5: required ResponseCode responseCode;
    6: optional map<string, ImpressionInfo> impressionInfo;
}