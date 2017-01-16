package com.chymeravr.enums;

import lombok.Getter;

/**
 * Created by rubbal on 16/1/17.
 */
public enum AppStore {
    GOOGLE_PLAY_STORE(1),
    OCULUS(2);

    @Getter
    private final int appStoreId;

    AppStore(int appStoreId) {
        this.appStoreId = appStoreId;
    }

    public static AppStore getAppStore(int appStoreId) {
        switch (appStoreId) {
            case 1:
                return GOOGLE_PLAY_STORE;
            case 2:
                return OCULUS;
            default:
                throw new IllegalArgumentException("Invalid App store Id");
        }
    }
}
