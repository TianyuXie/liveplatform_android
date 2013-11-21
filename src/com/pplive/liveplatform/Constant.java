package com.pplive.liveplatform;

import android.os.Build;

public final class Constant {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    //  public static final String TEST_PUSH_URL = "rtmp://10.0.0.200/live/android";
    public static final String TEST_PUSH_URL = "rtmp://183.129.205.101:1936/push/mobi124?ts=1384811567&token=f5a4c1e245c15e76970d28bdd9c01471";

    private Constant() {
    }
}
