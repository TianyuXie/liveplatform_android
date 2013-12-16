package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.util.DisplayUtil;

public class AnimDoor extends RelativeLayout {
    private static final int HORIZONTAL = 0;

    private static final int VERTICAL = 1;

    private ViewGroup mRoot;

    private ImageView mLeftDoorImageView;

    private ImageView mRightDoorImageView;

    private Animation mLCAnimation;

    private Animation mLOAnimation;

    private Animation mRCAnimation;

    private Animation mROAnimation;

    private float mFactor;

    private int mOrientaion;

    private int mDuration;

    private float mAnimX;

    public AnimDoor(Context context) {
        this(context, null);
    }

    public AnimDoor(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (ViewGroup) inflater.inflate(R.layout.widget_animdoor, this);
        mLeftDoorImageView = (ImageView) mRoot.findViewById(R.id.image_animdoor_left);
        mRightDoorImageView = (ImageView) mRoot.findViewById(R.id.image_animdoor_right);

        /* default values */
        mFactor = 1.0f;
        mOrientaion = HORIZONTAL;
        mDuration = 1000;
        mAnimX = 0.0f;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimDoor);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.AnimDoor_left_src:
                mLeftDoorImageView.setImageDrawable(a.getDrawable(attr));
                break;
            case R.styleable.AnimDoor_right_src:
                mRightDoorImageView.setImageDrawable(a.getDrawable(attr));
                break;
            case R.styleable.AnimDoor_factor:
                mFactor = a.getFloat(attr, 1.0f);
                break;
            case R.styleable.AnimDoor_orientation:
                mOrientaion = a.getInt(attr, 0);
                break;
            case R.styleable.AnimDoor_duration:
                mDuration = a.getInt(attr, 1000);
                break;
            case R.styleable.AnimDoor_visible:
                if (a.getBoolean(attr, false)) {
                    mLeftDoorImageView.setVisibility(VISIBLE);
                    mRightDoorImageView.setVisibility(VISIBLE);
                } else {
                    mLeftDoorImageView.setVisibility(GONE);
                    mRightDoorImageView.setVisibility(GONE);
                }
            }
        }
        a.recycle();
        setFactor();
    }

    public void setFactor() {
        setFactor(mFactor);
    }

    public void setFactor(float factor) {
        mFactor = factor;

        RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) mLeftDoorImageView.getLayoutParams();
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mRightDoorImageView.getLayoutParams();

        switch (mOrientaion) {
        case VERTICAL:
            llp.addRule(ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            llp.addRule(ALIGN_PARENT_LEFT, 0);
            rlp.addRule(ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.addRule(ALIGN_PARENT_RIGHT, 0);
            rlp.width = llp.width = LayoutParams.MATCH_PARENT;
            mAnimX = rlp.height = llp.height = (int) Math.round(DisplayUtil.getWidthPx(getContext()) / 2.0 * mFactor);
            mLeftDoorImageView.requestLayout();
            mRightDoorImageView.requestLayout();
            mLCAnimation = new TranslateAnimation(0.0f, 0.0f, mAnimX, 0.0f);
            mLOAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, mAnimX);
            mRCAnimation = new TranslateAnimation(0.0f, 0.0f, -mAnimX, 0.0f);
            mROAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -mAnimX);
            break;
        case HORIZONTAL:
        default:
            llp.addRule(ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            llp.addRule(ALIGN_PARENT_BOTTOM, 0);
            rlp.addRule(ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            rlp.addRule(ALIGN_PARENT_TOP, 0);
            rlp.height = llp.height = LayoutParams.MATCH_PARENT;
            mAnimX = rlp.width = llp.width = (int) Math.round(DisplayUtil.getWidthPx(getContext()) / 2.0 * mFactor);
            mLeftDoorImageView.requestLayout();
            mRightDoorImageView.requestLayout();
            mLCAnimation = new TranslateAnimation(-mAnimX, 0.0f, 0.0f, 0.0f);
            mLOAnimation = new TranslateAnimation(0.0f, -mAnimX, 0.0f, 0.0f);
            mRCAnimation = new TranslateAnimation(mAnimX, 0.0f, 0.0f, 0.0f);
            mROAnimation = new TranslateAnimation(0.0f, mAnimX, 0.0f, 0.0f);
            break;
        }
        mLCAnimation.setDuration(mDuration);
        mRCAnimation.setDuration(mDuration);
        mLOAnimation.setDuration(mDuration);
        mROAnimation.setDuration(mDuration);
        mLCAnimation.setFillAfter(true);
        mRCAnimation.setFillAfter(true);
        mLOAnimation.setFillAfter(true);
        mROAnimation.setFillAfter(true);
        mLCAnimation.setInterpolator(new LinearInterpolator());
        mRCAnimation.setInterpolator(new LinearInterpolator());
        mLOAnimation.setInterpolator(new LinearInterpolator());
        mROAnimation.setInterpolator(new LinearInterpolator());
    }

    public void setDoorDrawable(Drawable left, Drawable right) {
        mLeftDoorImageView.setImageDrawable(left);
        mRightDoorImageView.setImageDrawable(right);
        setFactor();
    }

    public void setDoorResource(int left, int right) {
        setDoorDrawable(getContext().getResources().getDrawable(left), getContext().getResources().getDrawable(right));
    }

    public void setDuration(long durationMillis) {
        if (mLCAnimation != null && mRCAnimation != null && mLOAnimation != null && mROAnimation != null) {
            mLCAnimation.setDuration(durationMillis);
            mRCAnimation.setDuration(durationMillis);
            mLOAnimation.setDuration(durationMillis);
            mROAnimation.setDuration(durationMillis);
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        if (mLCAnimation != null && mRCAnimation != null && mLOAnimation != null && mROAnimation != null) {
            mLCAnimation.setInterpolator(interpolator);
            mRCAnimation.setInterpolator(interpolator);
            mLOAnimation.setInterpolator(interpolator);
            mROAnimation.setInterpolator(interpolator);
        }
    }

    public void shut() {
        if (mRCAnimation != null && mLCAnimation != null) {
            mLeftDoorImageView.setVisibility(VISIBLE);
            mRightDoorImageView.setVisibility(VISIBLE);
            mLeftDoorImageView.startAnimation(mLCAnimation);
            mRightDoorImageView.startAnimation(mRCAnimation);
        }
    }

    public void hide() {
        mLeftDoorImageView.setVisibility(GONE);
        mRightDoorImageView.setVisibility(GONE);
        mRightDoorImageView.clearAnimation();
        mLeftDoorImageView.clearAnimation();
    }

    public void open() {
        if (mLOAnimation != null && mROAnimation != null) {
            mLeftDoorImageView.setVisibility(VISIBLE);
            mRightDoorImageView.setVisibility(VISIBLE);
            mRightDoorImageView.startAnimation(mROAnimation);
            mLeftDoorImageView.startAnimation(mLOAnimation);
        }
    }
    
    public float getAnimX() {
        return mAnimX;
    }
    
    public int getDuration() {
        return mDuration;
    }

    public void setShutDoorListener(AnimationListener listener) {
        mLCAnimation.setAnimationListener(listener);
    }

    public void setOpenDoorListener(AnimationListener listener) {
        mLOAnimation.setAnimationListener(listener);
    }
}
