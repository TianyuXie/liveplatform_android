package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;

public class SideBar extends LinearLayout implements SlidableContainer.OnSlideListener {
    private View mRoot;

    private Animation mShowAnimation;

    private Animation mHideAnimation;

    private boolean inAnimation;

    private boolean isShowing;

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = inflater.inflate(R.layout.widget_sidebar, this);
        mShowAnimation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.sidebar_show);
        mHideAnimation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.sidebar_hide);
    }

    public SideBar(Context context) {
        this(context, null);
    }

    public void hide(boolean gone) {
        if (!inAnimation && isShowing) {
            startAnimation(mHideAnimation);
            mRoot.setVisibility(gone ? GONE : INVISIBLE);
            isShowing = false;
        }
    }

    public void show() {
        if (!inAnimation && !isShowing) {
            mRoot.setVisibility(VISIBLE);
            isShowing = true;
            startAnimation(mShowAnimation);
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
    }

    @Override
    public void onSlide() {
        show();
    }

    @Override
    public void onSlideBack() {
        hide(true);
    }
}
