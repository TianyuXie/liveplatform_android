package com.pplive.liveplatform.ui.widget.slide;

import android.content.Context;
import android.util.AttributeSet;

public class SimpleSlidableContainer extends SlidableContainer {
    final static String TAG = "SimpleSlidableContainer";

    public SimpleSlidableContainer(Context context) {
        this(context, null);
    }

    public SimpleSlidableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFillAfter(true);
    }

    @Override
    public boolean slide() {
        if (!isAnimating() && !isSlided()) {
            startSlideAnimation();
            return true;
        }
        return false;
    }

    @Override
    public boolean slideBack() {
        if (!isAnimating() && isSlided()) {
            startSlideBackAnimation();
            return true;
        }
        return false;
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        switchStatus();
    }
}
