package com.pplive.liveplatform;

import android.os.Build;

public final class Constant {

    public static final boolean LARGER_THAN_OR_EQUAL_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    
    private Constant() {}
}
