package com.pplive.liveplatform.widget.attr;

import android.view.MotionEvent;

import com.pplive.liveplatform.widget.intercept.InterceptDetector;

public interface Interceptable {
    public void setInterceptDetector(InterceptDetector detector);

    public boolean onInterceptTouchEvent(MotionEvent ev);

    public boolean onTouchEvent(MotionEvent event);
}
