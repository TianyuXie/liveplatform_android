package com.pplive.liveplatform.dac.stat;

import android.os.Build;
import android.pplive.media.MeetSDK;

import com.pplive.liveplatform.dac.data.CommonData;
import com.pplive.liveplatform.dac.info.AppInfo;
import com.pplive.liveplatform.dac.info.DeviceInfo;
import com.pplive.liveplatform.dac.info.SessionInfo;
import com.pplive.sdk.MediaSDK;

public abstract class BaseDacStat extends DacStat implements CommonData {

    private static final long serialVersionUID = 1241740408396579165L;

    public BaseDacStat() {
        addValueItem(KEY_DEVICE_ID, DeviceInfo.getIMEI() + "|" + DeviceInfo.getWLANMac());
        addValueItem(KEY_DEVICE_BOARD, Build.BOARD);
        addValueItem(KEY_DEVICE_MODULE, Build.MODEL + "&" + Build.PRODUCT);
        addValueItem(KEY_OS_VERSION, DeviceInfo.getOSVersion());
        addValueItem(KEY_CPU_MODULE, DeviceInfo.getCPUModule());
        addValueItem(KEY_SCREEN_RESOLUTION, DeviceInfo.getScreenResolution());
        addValueItem(KEY_TERMINAL_CATEGORY, 4 /* aph */);
        addValueItem(KEY_USER_ID, DeviceInfo.getIMEI());
        addValueItem(KEY_TERMINAL_VERSION, AppInfo.getVersionName());
        addValueItem(KEY_SDK_VERSION, MediaSDK.getPPBoxVersion());
        addValueItem(KEY_PLAYER_VERSION, MeetSDK.getVersion());
        addValueItem(KEY_SESSION_ID, SessionInfo.getSessionId());
    }
}
