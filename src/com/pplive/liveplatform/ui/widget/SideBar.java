package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.pplive.liveplatform.R;

public class SideBar extends LinearLayout {
    private View mRoot;

    private Animation mShowAnimation;

    private Animation mHideAnimation;

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
        if (mRoot.getVisibility() == VISIBLE) {
            startAnimation(mHideAnimation);
            mRoot.setVisibility(gone ? GONE : INVISIBLE);
        }
    }

    public void show() {
        if (mRoot.getVisibility() != VISIBLE) {
            mRoot.setVisibility(VISIBLE);
            startAnimation(mShowAnimation);
        }
    }

}
