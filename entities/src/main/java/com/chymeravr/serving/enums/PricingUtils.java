package com.chymeravr.serving.enums;

import com.chymeravr.schemas.serving.PricingModel;

import static com.chymeravr.schemas.serving.PricingModel.CPC;
import static com.chymeravr.schemas.serving.PricingModel.CPM;

/**
 * Created by rubbal on 16/1/17.
 */
public class PricingUtils {

    private PricingUtils() {
    }

    public static int getPricingId(PricingModel pricingModel) {
        switch (pricingModel) {
            case CPC:
                return 1;
            case CPM:
                return 2;
            default:
                throw new IllegalArgumentException("Invalid pricing model");
        }
    }

    public static PricingModel getPricing(int pricingId) {
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
