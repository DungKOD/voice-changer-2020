package com.audioeffect.voicechanger.utils;

import android.content.Context;

import com.audioeffect.voicechanger.IMobileBilling;
import com.audioeffect.voicechanger.MobileBilling;

import java.util.ArrayList;
import java.util.List;

public class AppBilling implements IMobileBilling {

    public static final String SKU_PREMIUM_MONTHLY_TRIAL = "voice_changer_pro_trial";

    private static final List<String> SKU_PREMIUMS = new ArrayList<String>() {
        {
            add(SKU_PREMIUM_MONTHLY_TRIAL);
        }
    };

    @Override
    public String getAdFreeSku() {
        return null;
    }

    @Override
    public List<String> getFeatureSkus() {
        return SKU_PREMIUMS;
    }

    @Override
    public boolean isProductConsumable(String s) {
        return false;
    }

    @Override
    public boolean isPurchasedFeature(Context context) {
        for (String sku : SKU_PREMIUMS) {
            if (MobileBilling.isPurchasedSku(context, sku)) {
                return true;
            }
        }

        return false;
    }

}
