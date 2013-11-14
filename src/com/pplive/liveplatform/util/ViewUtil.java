package com.pplive.liveplatform.util;

import android.view.View;

public class ViewUtil {

    public static void showOrHide(final View v) {
        showOrHide(v, true);
    }
    
    public static void showOrHide(final View v, final boolean gone) {
        if (null == v) {
            return;
        }
        
        if (View.VISIBLE != v.getVisibility()) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(gone ? View.GONE : View.INVISIBLE);
        }
    }
    
    public static void setVisibility(final View v, final int flag) {
        if (null == v) {
            return;
        }
        
        v.setVisibility(flag != 0 ? View.VISIBLE : View.GONE);
    }
    
    private ViewUtil() {}
}
