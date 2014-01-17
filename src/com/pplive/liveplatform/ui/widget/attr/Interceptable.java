package com.pplive.liveplatform.ui.widget.attr;

import android.view.MotionEvent;

import com.pplive.liveplatform.ui.widget.intercept.InterceptDetector;

public interface Interceptable {
    public void setInterceptDetector(InterceptDetector detector);

    public boolean onInterceptTouchEvent(MotionEvent ev);

    public boolean onTouchEvent(MotionEvent event);
}
