package com.pplive.liveplatform.util;

import java.util.Locale;
import java.util.TimeZone;

public class GeoUtil {
    private static final String CHINA_STANDARD_TIME = "China Standard Time";

    public static String getTimeZoneName() {
        return TimeZone.getDefault().getDisplayName(Locale.US);
    }

    public static boolean isInChina() {
        return getTimeZoneName().equals(CHINA_STANDARD_TIME);
    }
}
