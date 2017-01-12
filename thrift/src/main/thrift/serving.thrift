namespace java com.chymeravr

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
struct RequestInfo {
    1: required string appId;
    2: required string placementId;
    3: required AdFormat format;
    4: required i32 OsId;
    5: required i32 OsVersion;
    6: required i32 deviceId;
    7: required i32 HmdId;
}

struct ImpressionInfo {
    1: required string impressionId;
    2: required string advertiserId;
    3: required string adgroupId;
    4: required string adId;
    5: required double costPrice;
    6: required double sellingPrice;
}

struct ServingLog {
    1: required string servingId;
    2: required i32 sdkVersion;
    3: required list<i32> experimentIds;
    4: required RequestInfo requestInfo;
    5: required ResponseCode responseCode;
    6: optional ImpressionInfo impressionInfo;
}