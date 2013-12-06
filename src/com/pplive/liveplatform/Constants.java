package com.pplive.liveplatform;

import android.os.Build;

public final class Constants {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    public static final boolean LARGER_THAN_OR_EQUAL_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public static final String PLATFORM_ANDROID_PHONE = "aph";

    public static final String TEST_COTK = "IUYh6uNebPXXeANzajRMr69rRiPO5IykpkflGv%2BQ9BtUtzBk7ngddgfqfYUcz3ZpJONIitVlMpyz%0AlQ64hp3wGerqjFJkZTnFKNsW9VymbU8XDuYGomFgYECPHPijagkulaPMKtBdyckBaAB74BYOwtky%0AM8QmMTIggHyh%2FGHGrvA%3D";

    //    public static final String LIVEPLATFORM_API_HOST = "172.16.6.47:80";

    public static final String LIVEPLATFORM_API_HOST = "api.liveplatform.pptv.com";

    public static final String PASSPORT_API_HOST = "passport.pptv.com";

    //    public static final int TEST_PORT = 8080;

    private Constants() {
    }
}
