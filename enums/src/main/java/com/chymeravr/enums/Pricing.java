package com.chymeravr.enums;

import lombok.Getter;

/**
 * Created by rubbal on 16/1/17.
 */
public enum Pricing {
    CPC(1),
    CPM(2);

    @Getter
    public final int pricingId;

    Pricing(int pricingId) {
        this.pricingId = pricingId;
    }

    public static Pricing getPricing(int pricingId) {
        switch (pricingId) {
            case 1:
                return CPC;
            case 2:
                return CPM;
            default:
                throw new IllegalArgumentException("Invalid pricing Id");
        }
    }
}
