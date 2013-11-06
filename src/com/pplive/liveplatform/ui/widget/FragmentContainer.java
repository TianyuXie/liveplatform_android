package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class FragmentContainer extends LinearLayout {

    private boolean isEnabled = true;

    public FragmentContainer(Context context) {
        super(context);
    }

    public FragmentContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (isEnabled) {
            return super.onInterceptTouchEvent(ev);
//        } else {
//            return true;
//        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        isEnabled = enabled;
    }
}
