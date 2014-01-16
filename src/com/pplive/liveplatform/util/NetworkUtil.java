package com.pplive.liveplatform.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isGpsEnable(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int type = telephonyManager.getNetworkType();
        switch (type) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return false; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE:
            return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_EHRPD:
            return true; // ~ 1-2 Mbps
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return true; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return true; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return true; // ~ 5 Mbps
        case TelephonyManager.NETWORK_TYPE_GPRS:
            return false; // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return true; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return true; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSPAP:
            return true; // ~ 10-20 Mbps
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return true; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_IDEN:
            return false; // ~ 25 kbps
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return true; // ~ 400-7000 kbps
        case TelephonyManager.NETWORK_TYPE_LTE:
            return true; // ~ 10+ Mbps
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            return false;
        default:
            return false;
        }
    }

}
