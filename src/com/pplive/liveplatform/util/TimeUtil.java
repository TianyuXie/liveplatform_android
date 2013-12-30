package com.pplive.liveplatform.util;

import java.util.Locale;

public class TimeUtil {
    public static final int MS_OF_SECOND = 1000;
    public static final int MS_OF_MIN = 60 * MS_OF_SECOND;
    public static final int MS_OF_HOUR = 60 * MS_OF_MIN;
    public static final int SECONDS_OF_DAY = 24 * 3600;
    public static final int SECONDS_OF_HOUR = 3600;

    public static String stringForTime(long timeMs) {
        if (timeMs <= 0) {
            return "00:00:00";
        }
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String stringForLongTime(long timeMs) {
        if (timeMs <= 0) {
            return "0时0分";
        }
        long totalSeconds = timeMs / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / SECONDS_OF_HOUR) % 24;
        long days = totalSeconds / SECONDS_OF_DAY;
        if (days > 0) {
            return String.format(Locale.US, "%d天%d时%d分", days, hours, minutes);
        } else {
            return String.format(Locale.US, "%d时%d分", hours, minutes);
        }
    }
}
