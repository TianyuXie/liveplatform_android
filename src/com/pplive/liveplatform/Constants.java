package com.pplive.liveplatform;

import android.os.Build;

public final class Constants {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    public static final boolean LARGER_THAN_OR_EQUAL_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    //    public static final String TEST_PUSH_URL = "/sdcard/ppflv/test.flv";
    //    public static final String TEST_PUSH_URL = "rtmp://10.0.0.200/live/android";
    //    public static final String TEST_PLAY_URL = "rtsp://127.0.0.1:5054/record.es?playlink=rtmp%3A%2F%2F10.0.0.200%2Flive%2Fandroid";

    public static final String TEST_PUSH_URL = "rtmp://172.16.6.32:1936/push/d59c3e22f22340689d1908e9eee8108c?ts=1385198274&token=95dea0fa857274983b9f9a93dc45f64a";

    public static final String TEST_PLAY_URL = "rtsp://127.0.0.1:5054/record.es?playlink=rtmp%3A%2F%2F183.129.205.101%3A1935%2Fflvplayback%2Fmobi124";

    public static final String TEST_COTK = "oeB87zWB3w%2BMxA%2BHVU0pBiVvl%2BTOmeQjECZ0fioqapUmGfqHfSujNZNPXdMVtm3x31Dgn674%2BSwb%0AjS7504rQkVl%2BPjVAXpJxe7X%2FH%2FL7jWQ6nlw%2BaPqloHncjpBik5QI0fcLc2oVMHHbsFc%2BE8mGhyae%0AGDH8eD%2Bdzgg62NxoW2s%3D";

    //    public static final String LIVEPLATFORM_API_HOST = "172.16.6.64:80";

    public static final String LIVEPLATFORM_API_HOST = "api.liveplatform.pptv.com";

    public static final String PASSPORT_API_HOST = "passport.pptv.com";

    //    public static final int TEST_PORT = 8080;

    private Constants() {
    }
}
