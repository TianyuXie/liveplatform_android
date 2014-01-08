package com.pplive.liveplatform.dac.info;

import java.util.UUID;

import android.util.Log;

public class SessionInfo {

    private static final String TAG = SessionInfo.class.getSimpleName();

    private static String sSessionId = "unknown";

    public static void init() {
        reset();
    }

    public static void reset() {
        sSessionId = UUID.randomUUID().toString();

        Log.d(TAG, "Session Id: " + sSessionId);
    }

    public static String getSessionId() {
        return sSessionId;
    }

    private SessionInfo() {

    }
}
