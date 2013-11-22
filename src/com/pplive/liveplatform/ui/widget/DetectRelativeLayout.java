package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class DetectRelativeLayout extends RelativeLayout {

    public DetectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public DetectRelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("DetectRelativeLayout:onSizeChanged", String.format("%d, %d, %d, %d", w, h, oldw, oldh));
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("DetectRelativeLayout:onLayout", String.format("%d, %d, %d, %d", l, t, r, b));
        super.onLayout(changed, l, t, r, b);
    }

}
