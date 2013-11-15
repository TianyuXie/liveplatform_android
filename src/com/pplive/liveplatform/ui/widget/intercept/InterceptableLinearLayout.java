package com.pplive.liveplatform.ui.widget.intercept;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptableLinearLayout extends LinearLayout implements Interceptable {
    static final String TAG = "InterceptableLinearLayout";

    public InterceptableLinearLayout(Context context) {
        this(context, null);
    }

    public InterceptableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private InterceptDetector mInterceptDetector;

    public void setGestureDetector(InterceptDetector detector) {
        this.mInterceptDetector = detector;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent");
        if (mInterceptDetector != null && mInterceptDetector.onTouchEvent(ev)) {
            Log.d(TAG, "Intercept");
            return true;
        } else {
            Log.d(TAG, "Pass");
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent");
        if (mInterceptDetector != null && mInterceptDetector.onTouchEvent(event)) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
