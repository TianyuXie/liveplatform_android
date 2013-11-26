
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

    public static void setVisibility(final View v, final int flag, final boolean gone) {
        if (v == null) {
            return;
        }
        if (flag == 0x0) {
            v.setVisibility(gone ? View.GONE : View.INVISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void setVisibility(final View v, final int flag) {
        setVisibility(v, flag, true);
    }

    public static void showLayoutDelay(final View v, final int timeout) {
        v.postDelayed(new Runnable() {
            public void run() {
                v.setVisibility(View.VISIBLE);
                v.requestLayout();
            }
        }, timeout);
    }

    public static void requestLayoutDelay(final View v, final int timeout) {
        v.postDelayed(new Runnable() {
            public void run() {
                v.requestLayout();
            }
        }, timeout);
    }

    private ViewUtil() {
    }
}
