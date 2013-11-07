package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.pplive.liveplatform.util.DisplayUtil;

public class FragmentContainer extends LinearLayout {
    final static String TAG = "FragmentContainer";

    private static final float SCALE_PERCENT = 0.95f;

    private static final float SLIDE_DP = 100.0f;

    private static final int SLIDE_DURATION = 300;

    private Animation stayAnimation;

    private AnimationSet slideAnimationSet;

    private AnimationSet slideBackAnimationSet;

    private int mScrollX;

    private int mAnimX;

    private boolean isSlided;

    private boolean inAnimation;

    private boolean inStay;

    public FragmentContainer(Context context) {
        this(context, null);
    }

    public FragmentContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAnimX = Math.round(DisplayUtil.dp2px(context, SLIDE_DP) - (1.0f - SCALE_PERCENT)
                * DisplayUtil.getWidthPx(context));

        mScrollX = Math.round(mAnimX / SCALE_PERCENT);

        slideAnimationSet = new AnimationSet(true);
        slideBackAnimationSet = new AnimationSet(true);
        slideAnimationSet.setDuration(SLIDE_DURATION);
        slideBackAnimationSet.setDuration(SLIDE_DURATION);

        slideAnimationSet.addAnimation(new ScaleAnimation(1.0f, SCALE_PERCENT, 1.0f, SCALE_PERCENT,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f));
        slideAnimationSet.addAnimation(new TranslateAnimation(0.0f, mAnimX, 0.0f, 0.0f));
        slideBackAnimationSet.addAnimation(new ScaleAnimation(SCALE_PERCENT, 1.0f, SCALE_PERCENT,
                1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f));
        slideBackAnimationSet.addAnimation(new TranslateAnimation(mAnimX, 0.0f, 0.0f, 0.0f));

        stayAnimation = new ScaleAnimation(SCALE_PERCENT, SCALE_PERCENT, SCALE_PERCENT,
                SCALE_PERCENT, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        stayAnimation.setDuration(1);
        stayAnimation.setFillAfter(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;
        }
    }

    public void slide() {
        if (!inAnimation && !isSlided) {
            setVisibility(GONE);
            startAnimation(slideAnimationSet);
        }
    }

    public void slideBack() {
        if (!inAnimation && isSlided) {
            setVisibility(GONE);
            scrollBy(mScrollX, 0);
            startAnimation(slideBackAnimationSet);
        }
    }

    @Override
    public void startAnimation(Animation animation) {
        inAnimation = true;
        super.startAnimation(animation);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        inAnimation = false;
        if (!inStay) {
            clearAnimation();
            if (!isSlided) {
                scrollBy(-mScrollX, 0);
                startAnimation(stayAnimation);
                inStay = true;
            }
            isSlided = !isSlided;
            setVisibility(VISIBLE);
        } else {
            inStay = false;
        }
    }
    
    public boolean isSlided() {
        return isSlided;
    }
}
