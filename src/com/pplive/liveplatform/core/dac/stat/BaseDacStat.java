package com.pplive.liveplatform.core.dac.stat;

import java.util.UUID;

import android.os.Build;
import android.pplive.media.MeetSDK;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.dac.data.CommonData;
import com.pplive.liveplatform.core.dac.info.AppInfo;
import com.pplive.liveplatform.core.dac.info.DeviceInfo;
import com.pplive.liveplatform.core.dac.info.LocationInfo;
import com.pplive.liveplatform.core.dac.info.SessionInfo;
import com.pplive.liveplatform.core.dac.info.UserInfo;
import com.pplive.sdk.MediaSDK;

public abstract class BaseDacStat extends DacStat implements CommonData {

    private static final long serialVersionUID = 1241740408396579165L;

    public BaseDacStat() {
        addValueItem(KEY_DEVICE_ID, UUID.nameUUIDFromBytes((DeviceInfo.getIMEI() + "|" + DeviceInfo.getWLANMac()).getBytes()));
        addValueItem(KEY_DEVICE_BOARD, Build.BOARD);
        addValueItem(KEY_DEVICE_MODULE, Build.MODEL + "|" + Build.PRODUCT);
        addValueItem(KEY_OS_VERSION, DeviceInfo.getOSVersion());
        addValueItem(KEY_CPU_MODULE, DeviceInfo.getCPUModule());
        addValueItem(KEY_SCREEN_RESOLUTION, DeviceInfo.getScreenResolution());
        addValueItem(KEY_TERMINAL_CATEGORY, Constants.PLATFORM_ANDROID_PHONE);
        addValueItem(KEY_USER_ID, UserInfo.getUserName());
        addValueItem(KEY_TERMINAL_VERSION, AppInfo.getVersionName());
        addValueItem(KEY_SDK_VERSION, MediaSDK.getPPBoxVersion());
        addValueItem(KEY_PLAYER_VERSION, MeetSDK.getVersion());
        addValueItem(KEY_SESSION_ID, SessionInfo.getSessionId());
        addValueItem(KEY_GPS_X, LocationInfo.getLongitude());
        addValueItem(KEY_GPS_Y, LocationInfo.getLatitude());
    }
}
