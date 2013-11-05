package com.pplive.liveplatform.util;

import java.util.UUID;

public class StringUtil {
    public final static String EMPTY_STRING = "";

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().equals(EMPTY_STRING);
    }

    public static boolean notNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static String safeStringUnknown(String str) {
        // If the str is null or empty, return unknown
        if (isNullOrEmpty(str)) {
            return "Unknown";
        } else {
            return str;
        }
    }

    public static String safeStringEmpty(String str) {
        if (isNullOrEmpty(str)) {
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

}
