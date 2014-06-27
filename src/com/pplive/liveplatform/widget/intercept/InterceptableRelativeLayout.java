package com.pplive.liveplatform.widget.intercept;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.pplive.liveplatform.widget.attr.Interceptable;

public class InterceptableRelativeLayout extends RelativeLayout implements Interceptable {
    static final String TAG = "InterceptableRelativeLayout";

    public InterceptableRelativeLayout(Context context) {
        super(context);
    }

    public InterceptableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private InterceptDetector mInterceptDetector;

    @Override
    public void setInterceptDetector(InterceptDetector detector) {
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