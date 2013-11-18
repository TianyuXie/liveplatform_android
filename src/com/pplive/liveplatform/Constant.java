package com.pplive.liveplatform;

import android.os.Build;

public final class Constant {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    public static final String TEST_PUSH_URL = "rtmp://10.0.0.200/live/android";
    public static final String TEST_PLAY_URL = "rtsp://127.0.0.1:5054/record.es?playlink=rtmp%3A%2F%2F10.0.0.200%2Flive%2Fandroid";
    
    private Constant() {
    }
}
