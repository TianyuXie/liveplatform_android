package com.pplive.liveplatform.ui.widget.slide;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.pplive.liveplatform.util.DisplayUtil;

public abstract class SlidableContainer extends LinearLayout {
    final static String TAG = "SlidableContainer";

    protected static final float SCALE_PERCENT = 0.95f;

    private static final int SLIDE_DURATION = 250;

    private static final float SLIDE_DP = 100.0f;

    private AnimationSet mSlideAnimationSet;

    private AnimationSet mSlideBackAnimationSet;

    private boolean mSlided;

    private boolean mAnimating;

    private float mAnimX;

    private Collection<OnSlideListener> mOnSlideListeners;

    public SlidableContainer(Context context) {
        this(context, null);
    }

    public SlidableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAnimX = DisplayUtil.dp2px(context, SLIDE_DP) - (1.0f - SCALE_PERCENT) * DisplayUtil.getWidthPx(context);

        mSlideAnimationSet = new AnimationSet(true);
        mSlideBackAnimationSet = new AnimationSet(true);
        mSlideAnimationSet.setInterpolator(new DecelerateInterpolator());
        mSlideBackAnimationSet.setInterpolator(new DecelerateInterpolator());
        mSlideAnimationSet.setDuration(SLIDE_DURATION);
        mSlideBackAnimationSet.setDuration(SLIDE_DURATION);

        mSlideAnimationSet.addAnimation(new ScaleAnimation(1.0f, SCALE_PERCENT, 1.0f, SCALE_PERCENT, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        mSlideAnimationSet.addAnimation(new TranslateAnimation(0.0f, mAnimX, 0.0f, 0.0f));
        mSlideBackAnimationSet.addAnimation(new ScaleAnimation(SCALE_PERCENT, 1.0f, SCALE_PERCENT, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        mSlideBackAnimationSet.addAnimation(new TranslateAnimation(mAnimX, 0.0f, 0.0f, 0.0f));

        mOnSlideListeners = new ArrayList<SlidableContainer.OnSlideListener>();
    }

    public abstract boolean slide();

    public abstract boolean slideBack();

    @Override
    public void startAnimation(Animation animation) {
        Log.d(TAG, "startAnimation");
        mAnimating = true;
        super.startAnimation(animation);
    }

    @Override
    protected void onAnimationEnd() {
        Log.d(TAG, "onAnimationEnd");
        mAnimating = false;
        super.onAnimationEnd();
    }

    protected boolean isAnimating() {
        return mAnimating;
    }

    protected void startSlideAnimation() {
        startAnimation(mSlideAnimationSet);
        notifySlide();
    }

    protected void startSlideBackAnimation() {
        startAnimation(mSlideBackAnimationSet);
        notifySlideBack();
    }

    protected float getAnimX() {
        return mAnimX;
    }

    protected boolean isSlided() {
        return mSlided;
    }

    protected void switchStatus() {
        mSlided = !mSlided;
    }

    protected void setFillAfter(boolean fillAfter) {
        mSlideAnimationSet.setFillAfter(fillAfter);
        mSlideBackAnimationSet.setFillAfter(fillAfter);
    }

    public void attachOnSlideListener(OnSlideListener listener) {
        if (listener != null) {
            mOnSlideListeners.add(listener);
        }
    }

    public void detachOnSlideListener(OnSlideListener listener) {
        if (listener != null) {
            mOnSlideListeners.remove(listener);
        }
    }

    public void clearOnSlideListeners() {
        mOnSlideListeners.clear();
    }

    private void notifySlide() {
        for (OnSlideListener listener : mOnSlideListeners) {
            listener.onSlide();
        }
    }

    private void notifySlideBack() {
        for (OnSlideListener listener : mOnSlideListeners) {
            listener.onSlideBack();
        }
    }

    /**
     * Interface definition for a callback to be invoked when the container is slided.
     */
    public interface OnSlideListener {
        /**
         * Called when the container has been slided.
         * 
         */
        void onSlide();

        /**
         * Called when the container has been slided back.
         * 
         */
        void onSlideBack();
    }

}
