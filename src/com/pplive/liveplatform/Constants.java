package com.pplive.liveplatform;

import android.os.Build;

public final class Constants {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    public static final boolean LARGER_THAN_OR_EQUAL_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public static final String PLATFORM_ANDROID_PHONE = "aph";

    public static final String TEST_COTK = "hHIHsC5cf4xQv7vnfZ2dw1fVchUA7MGnMkMu%2FL%2FolXjgTh5iFxwxhBHdQsRQKX4ejEYp16zH9Lwd%0AWRxQpVoGnyLqkH6bRnt2ujH9%2BYzVx0mY9A9J%2FP%2B2dnE%2B7E8g3aKL1AkWCLBbjKs9L5MgGFli79fB%0A5rZunz%2FIjWtLeP1PeDQ%3D";

    //    public static final String LIVEPLATFORM_API_HOST = "172.16.6.47:80";

    public static final String LIVEPLATFORM_API_HOST = "api.liveplatform.pptv.com";

    public static final String PASSPORT_API_HOST = "passport.pptv.com";
    
    public static final String SC_API_HOST = "172.16.205.230";

    //    public static final int TEST_PORT = 8080;

    private Constants() {
    }
}
