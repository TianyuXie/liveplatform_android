package com.pplive.liveplatform;

import android.os.Build;

public final class Constants {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    public static final boolean LARGER_THAN_OR_EQUAL_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public static final String PLATFORM_ANDROID_PHONE = "aph";

    public static final String TEST_COTK = "oeB87zWB3w%2BMxA%2BHVU0pBiVvl%2BTOmeQjECZ0fioqapUmGfqHfSujNZNPXdMVtm3x31Dgn674%2BSwb%0AjS7504rQkVl%2BPjVAXpJxe7X%2FH%2FL7jWQ6nlw%2BaPqloHncjpBik5QI0fcLc2oVMHHbsFc%2BE8mGhyae%0AGDH8eD%2Bdzgg62NxoW2s%3D";

    //    public static final String LIVEPLATFORM_API_HOST = "172.16.6.64:80";

    public static final String LIVEPLATFORM_API_HOST = "api.liveplatform.pptv.com";

    public static final String PASSPORT_API_HOST = "passport.pptv.com";

    //    public static final int TEST_PORT = 8080;

    private Constants() {
    }
}
