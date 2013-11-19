package com.pplive.liveplatform;

import android.os.Build;

public final class Constants {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    
    public static final boolean LARGER_THAN_OR_EQUAL_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    //    public static final String TEST_PUSH_URL = "/sdcard/ppflv/test.flv";
    public static final String TEST_PUSH_URL = "rtmp://10.0.0.200/live/android";
    //    public static final String TEST_PUSH_URL = "rtmp://183.129.205.101:1936/push/mobi123?ts=1385740800&token=e031046b2c3612bb2059ab87f08cbb44";
    //    public static final String TEST_PUSH_URL = "rtmp://183.129.205.101:1936/push/mobi124?ts=1384811567&token=f5a4c1e245c15e76970d28bdd9c01471";

    public static final String TEST_PLAY_URL = "rtsp://127.0.0.1:5054/record.es?playlink=rtmp%3A%2F%2F10.0.0.200%2Flive%2Fedgavin";

    private Constants() {
    }
}
