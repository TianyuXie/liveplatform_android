package com.pplive.liveplatform;

import android.os.Build;

public interface Constants {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    public static final boolean LARGER_THAN_OR_EQUAL_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    public static final boolean LARGER_THAN_OR_EQUAL_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public static final String PLATFORM_ANDROID_PHONE = "aph";

    public static final String DEFAULT_CONAME_PPTV = "pptv";

    public static final String PASSPORT_API_HOST = "api.passport.pptv.com";

    public static final String LIVEPLATFORM_API_HOST = "api.liveplatform.pptv.com";

    public static final String LIVEPLATFORM_API_CDN_HOST = "apicdn.liveplatform.pptv.com";

    public static final String SC_API_HOST = "api.sc.pptv.com";

    public static final String BASE_SHARE_LINK_URL = "http://i.pptv.com/play/";

}
