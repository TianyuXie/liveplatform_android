package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.attr.IHidable;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;

public class SideBar extends LinearLayout implements SlidableContainer.OnSlideListener, IHidable {
    private View mRoot;

    private Animation mShowAnimation;

    private Animation mHideAnimation;

    private boolean mAnimating;

    private boolean mShowing;

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = inflater.inflate(R.layout.widget_sidebar, this);

        int id = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideBar);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.SideBar_anim_show:
                id = a.getResourceId(attr, -1);
                if (id > 0) {
                    mShowAnimation = AnimationUtils.loadAnimation(context, id);
                }
                break;
            case R.styleable.SideBar_anim_hide:
                id = a.getResourceId(attr, -1);
                if (id > 0) {
                    mHideAnimation = AnimationUtils.loadAnimation(context, id);
                }
                break;
            }
        }
        a.recycle();
        mShowing = (getVisibility() == VISIBLE);
    }

    public SideBar(Context context) {
        this(context, null);
    }

    @Override
    public void hide(boolean gone) {
        if (!mAnimating && mShowing && mHideAnimation != null) {
            startAnimation(mHideAnimation);
            mRoot.setVisibility(gone ? GONE : INVISIBLE);
            mShowing = false;
        }
    }

    @Override
    public void show() {
        if (!mAnimating && !mShowing && mShowAnimation != null) {
            mRoot.setVisibility(VISIBLE);
            mShowing = true;
            startAnimation(mShowAnimation);
        }
    }

    @Override
    @Deprecated
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            mShowing = true;
        } else {
            mShowing = false;
        }
        super.setVisibility(visibility);
    }

    @Override
    public void startAnimation(Animation animation) {
        mAnimating = true;
        super.startAnimation(animation);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        mAnimating = false;
    }

    @Override
    public void onSlide() {
        show();
    }

    @Override
    public void onSlideBack() {
        hide(true);
    }

    @Override
    public void hide() {
        hide(true);
    }
}
