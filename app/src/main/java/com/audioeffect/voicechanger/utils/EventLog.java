package com.audioeffect.voicechanger.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.audioeffect.voicechanger.MobileAnalytics;

public class EventLog {
    private static final String TAG = "EventLog";

    public static final String OFFER_FROM_AUTO_SHOW = "offer_from_auto_show";
    public static final String OFFER_FROM_SAVE = "offer_from_save";

    public static final String OFFER_QUERY_SUCCESS = "offer_query_success";
    public static final String OFFER_QUERY_FAILED = "offer_query_failed";
    public static final String OFFER_QUERY_SKU_NOT_FOUND = "offer_query_sku_not_found";

    public static final String OFFER_1_SHOW = "offer_1_show";
    public static final String OFFER_1_CONTINUE = "offer_1_continue";
    public static final String OFFER_1_PURCHASE_FAILED = "offer_1_purchase_failed";
    public static final String OFFER_1_PURCHASE_SUCCESS = "offer_1_purchase_success";

    public static final String OFFER_2_SHOW = "offer_2_show";
    public static final String OFFER_2_CONTINUE = "offer_2_continue";
    public static final String OFFER_2_PURCHASE_FAILED = "offer_2_purchase_failed";
    public static final String OFFER_2_PURCHASE_SUCCESS = "offer_2_purchase_success";


    // Event offer
    public static void offer(Context context, String event, String from) {
        offer(context, event, from, null);
    }

    public static void offer(Context context, String event, String from, String errorMsg) {
        Bundle bundle = new Bundle();
        bundle.putString("offer_from", from != null ? from : "");
        bundle.putString("offer_err", errorMsg != null ? errorMsg : "");
        sendLog(context, event, bundle);
    }

    // Private
    private static void sendLog(Context context, String event, Bundle bundle) {
        if (context == null || TextUtils.isEmpty(event)) {
            return;
        }
        if (bundle == null) {
            bundle = new Bundle();
        }

        MobileAnalytics.logEvent(context, event, bundle);
    }

}
