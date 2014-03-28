package com.pplive.liveplatform.core.dac.info;

import java.text.MessageFormat;

import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class DeviceInfo {

    private static final String TAG = DeviceInfo.class.getSimpleName();

    private static String sOSVersion = "unknown";

    private static String sCPUModule = "unknown";

    private static String sScreenResolution = "unknown";

    private static String sIMEI = "unknown";

    private static String sWLANMac = "unknown";

    public static void init(Context context) {

        sOSVersion = Build.VERSION.RELEASE;
        sCPUModule = Build.CPU_ABI;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sIMEI = TextUtils.isEmpty(tm.getDeviceId()) ? sIMEI : tm.getDeviceId();

        WindowManager windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        sScreenResolution = MessageFormat.format("{0,number,#}*{1,number,#}", metrics.heightPixels, metrics.widthPixels);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (null != wifiInfo) {
            sWLANMac = TextUtils.isEmpty(wifiInfo.getMacAddress()) ? sWLANMac : wifiInfo.getMacAddress();
        }

        Log.d(TAG, "IMEI: " + sIMEI);
        Log.d(TAG, "OS Version: " + sOSVersion);
        Log.d(TAG, "CPU Module: " + sCPUModule);
        Log.d(TAG, "Screen Resolution: " + sScreenResolution);
        Log.d(TAG, "WLAN Mac: " + sWLANMac);
    }

    public static String getOSVersion() {
        return sOSVersion;
    }

    public static String getCPUModule() {
        return sCPUModule;
    }

    public static String getScreenResolution() {
        return sScreenResolution;
    }

    public static String getIMEI() {
        return sIMEI;
    }

    public static String getWLANMac() {
        return sWLANMac;
    }

    private DeviceInfo() {

    }
}
