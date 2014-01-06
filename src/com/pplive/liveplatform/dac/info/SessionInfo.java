package com.pplive.liveplatform.dac.info;

import org.apache.commons.codec.digest.DigestUtils;

import android.util.Log;

import com.pplive.liveplatform.util.Hex;

public class SessionInfo {
    
    private static final String TAG = SessionInfo.class.getSimpleName();
    
    private static String sSessionId = "unknown";
    
    public static void init() {
        genSessionID();
    }
    
    public static void reset() {
        genSessionID();
    }
    
    private static void genSessionID() {
        long curMillis = System.currentTimeMillis();
        sSessionId = new String(Hex.encode(DigestUtils.md5(curMillis + "&" + DeviceInfo.getIMEI() + "&" + DeviceInfo.getWLANMac())));
        
        Log.d(TAG, "Session Id: " + sSessionId);
    }
    
    public static String getSessionId() {
        return sSessionId;
    }

    private SessionInfo() {
        
    }
}
