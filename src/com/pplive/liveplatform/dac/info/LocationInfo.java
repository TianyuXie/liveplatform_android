package com.pplive.liveplatform.dac.info;

import android.util.Log;

import com.pplive.liveplatform.location.Locator;
import com.pplive.liveplatform.util.StringUtil;

public class LocationInfo {
    static final String TAG = LocationInfo.class.getSimpleName();

    private static Locator.LocationData sData;

    public static boolean isUpdated() {
        return sData != null;
    }

    public static void reset() {
        sData = null;
    }

    public static void updateData(Locator.LocationData data) {
        Log.d(TAG, String.format("%f|%f|%s", data.getLongitude(), data.getLatitude(), data.toString()));
        sData = data;
    }

    public static double getLongitude() {
        return sData == null ? -1.0 : sData.getLongitude();
    }

    public static double getLatitude() {
        return sData == null ? -1.0 : sData.getLatitude();
    }

    public static String getProvince() {
        return (sData == null || StringUtil.isNullOrEmpty(sData.getProvince())) ? "unknown" : sData.getProvince();
    }

    public static String getCity() {
        return (sData == null || StringUtil.isNullOrEmpty(sData.getCity())) ? "unknown" : sData.getCity();
    }

    public static String getDistrict() {
        return (sData == null || StringUtil.isNullOrEmpty(sData.getDistrict())) ? "unknown" : sData.getDistrict();
    }

    public static String getLocation() {
        return sData == null ? "unknown" : sData.toString();
    }

    private LocationInfo() {

    }
}
