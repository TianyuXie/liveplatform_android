package com.pplive.liveplatform.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static final int MS_OF_SECOND = 1000;
    public static final int MS_OF_MIN = 60 * MS_OF_SECOND;
    public static final int MS_OF_HOUR = 60 * MS_OF_MIN;
    public static final int SECONDS_OF_DAY = 24 * 3600;
    public static final int SECONDS_OF_HOUR = 3600;

    public static String stringForTimeMin(long timeMs) {
        if (timeMs <= 0) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    public static String stringForTimeHour(long timeMs) {
        if (timeMs <= 0) {
            return "00:00:00";
        }
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String stringForCountdown(long timeMs) {
        if (timeMs <= 0) {
            return "0时0分";
        }
        long totalSeconds = timeMs / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / SECONDS_OF_HOUR) % 24;
        long days = totalSeconds / SECONDS_OF_DAY;
        if (days > 0) {
            return String.format(Locale.US, "%d天%d时", days, hours);
        } else {
            return String.format(Locale.US, "%d时%d分", hours, minutes);
        }
    }

    public static String stamp2String(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        return format.format(new Date(time));
    }

    public static String stamp2StringShort(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-M-d", Locale.US);
        return format.format(new Date(time));
    }

    public static boolean isSameDay(long milli1, long milli2) {

        return isSameDay(new Date(milli1), new Date(milli2));
    }

    public static boolean isSameDay(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

}
