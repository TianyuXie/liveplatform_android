package com.pplive.liveplatform.util;

import java.util.UUID;

import android.content.res.Resources;

public class StringUtil {
    public final static String EMPTY_STRING = "";

    public final static String NULL_STRING = "null";

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.equals(NULL_STRING) || str.trim().equals(EMPTY_STRING);
    }

    public static boolean notNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static String safeString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public static String newGuid() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }
    
    public static String getRes(int resid){
        return Resources.getSystem().getString(resid);
    }

}
