package com.pplive.liveplatform.widget.slide;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.util.DisplayUtil;

public abstract class SlidableContainer extends LinearLayout {
    final static String TAG = "SlidableContainer";

    private AnimationSet mSlideAnimationSet;

    private AnimationSet mSlideBackAnimationSet;

    private boolean mSlided;

    private boolean mAnimating;

    private float mAnimX;
    
    protected float mScalePercent;

    private Collection<OnSlideListener> mOnSlideListeners;

    public SlidableContainer(Context context) {
        this(context, null);
    }

    public SlidableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mOnSlideListeners = new ArrayList<SlidableContainer.OnSlideListener>();

        /* default values */
        mScalePercent = 1.0f;
        int duration = 0;
        int distance = 0;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidableContainer);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.SlidableContainer_duration:
                duration = a.getInt(attr, 0);
                break;
            case R.styleable.SlidableContainer_distance:
                distance = a.getDimensionPixelSize(attr, 0);
                break;
            case R.styleable.SlidableContainer_scale_percent:
                mScalePercent = a.getFloat(attr, 1.0f);
                break;
            }
        }
        a.recycle();

        mAnimX = distance - (1.0f - mScalePercent) * DisplayUtil.getWidthPx(context);

        mSlideAnimationSet = new AnimationSet(true);
        mSlideBackAnimationSet = new AnimationSet(true);
        mSlideAnimationSet.setInterpolator(new DecelerateInterpolator());
        mSlideBackAnimationSet.setInterpolator(new DecelerateInterpolator());
        mSlideAnimationSet.setDuration(duration);
        mSlideBackAnimationSet.setDuration(duration);

        mSlideAnimationSet.addAnimation(new ScaleAnimation(1.0f, mScalePercent, 1.0f, mScalePercent, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        mSlideAnimationSet.addAnimation(new TranslateAnimation(0.0f, mAnimX, 0.0f, 0.0f));
        mSlideBackAnimationSet.addAnimation(new ScaleAnimation(mScalePercent, 1.0f, mScalePercent, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        mSlideBackAnimationSet.addAnimation(new TranslateAnimation(mAnimX, 0.0f, 0.0f, 0.0f));

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
