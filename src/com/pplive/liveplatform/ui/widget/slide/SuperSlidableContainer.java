package com.pplive.liveplatform.ui.widget.slide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class SuperSlidableContainer extends SlidableContainer {
    final static String TAG = "SuperSlidableContainer";

    private Animation mStayAnimation;

    private int mScrollX;

    private boolean mStay;

    public SuperSlidableContainer(Context context) {
        this(context, null);
    }

    public SuperSlidableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrollX = Math.round(getAnimX() / SCALE_PERCENT);
        mStayAnimation = new ScaleAnimation(SCALE_PERCENT, SCALE_PERCENT, SCALE_PERCENT, SCALE_PERCENT, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mStayAnimation.setDuration(1);
        mStayAnimation.setFillAfter(true);
    }

    @Override
    public boolean slide() {
        if (!isAnimating() && !isSlided()) {
            setVisibility(GONE);
            startSlideAnimation();
            return true;
        }
        return false;
    }

    @Override
    public boolean slideBack() {
        if (!isAnimating() && isSlided()) {
            setVisibility(GONE);
            scrollBy(mScrollX, 0);
            startSlideBackAnimation();
            return true;
        }
        return false;
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        if (!mStay) {
            clearAnimation();
            if (!isSlided()) {
                scrollBy(-mScrollX, 0);
                startAnimation(mStayAnimation);
                mStay = true;
            }
            switchStatus();
            setVisibility(VISIBLE);
        } else {
            mStay = false;
        }
    }
}
