package com.pplive.liveplatform.ui.widget.intercept;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptableLinearLayout extends LinearLayout implements Interceptable {
    static final String TAG = "InterceptableLinearLayout";

    private GestureDetector mGestureDetector;

    public InterceptableLinearLayout(Context context) {
        this(context, null);
    }

    public InterceptableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGestureDetector(GestureDetector detector) {
        this.mGestureDetector = detector;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent");
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent");
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

}
