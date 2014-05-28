package com.pplive.liveplatform.util;

import java.util.Calendar;

import android.content.Context;

import com.pplive.liveplatform.R;

public class TimeHelper {

    public static String getAboutStartTime(Context context, long startTime) {
        Calendar calendar = Calendar.getInstance();

        int yearOfNow = calendar.get(Calendar.YEAR);
        int monthOfNow = calendar.get(Calendar.MONTH) + 1;
        int dayOfNow = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfNow = calendar.get(Calendar.MINUTE);
        int secondOfNow = calendar.get(Calendar.SECOND);

        calendar.setTimeInMillis(startTime);

        int yearOfStart = calendar.get(Calendar.YEAR);
        int monthOfStart = calendar.get(Calendar.MONTH) + 1;
        int dayOfStart = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfStart = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfStart = calendar.get(Calendar.MINUTE);
        int secondOfStart = calendar.get(Calendar.SECOND);

        String result = null;
        if (yearOfNow - yearOfStart > 0) {
            result = context.getResources().getString(R.string.fmt_start_time_year, (yearOfNow - yearOfStart));
        } else if (monthOfNow - monthOfStart > 0) {
            result = context.getResources().getString(R.string.fmt_start_time_month, (monthOfNow - monthOfStart));
        } else if (dayOfNow - dayOfStart > 0) {
            result = context.getResources().getString(R.string.fmt_start_time_day, (dayOfNow - dayOfStart));
        } else if (hourOfNow - hourOfStart > 0) {
            result = context.getResources().getString(R.string.fmt_start_time_hour, (hourOfNow - hourOfStart));
        } else if (minuteOfNow - minuteOfStart > 0) {
            result = context.getResources().getString(R.string.fmt_start_time_minute, (minuteOfNow - minuteOfStart));
        } else if (secondOfNow - secondOfStart > 0) {
            result = context.getResources().getString(R.string.fmt_start_time_second, (secondOfNow - secondOfStart));
        } else {
            result = context.getResources().getString(R.string.fmt_start_time_second, 1);
        }

        return result;
    }
}
