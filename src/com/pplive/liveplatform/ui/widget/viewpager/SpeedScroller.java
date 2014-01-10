package com.pplive.liveplatform.ui.widget.viewpager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class SpeedScroller extends Scroller {

    private int mDuration = 0;

    public SpeedScroller(Context context) {
        super(context);
    }

    public SpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        if (mDuration == 0) {
            super.startScroll(startX, startY, dx, dy, duration);
        } else {
            // Ignore received duration
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        if (mDuration == 0) {
            super.startScroll(startX, startY, dx, dy);
        } else {
            // Ignore received duration
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    public void setDuration(int duration) {
        if (duration >= 0) {
            mDuration = duration;
        }
    }
}
