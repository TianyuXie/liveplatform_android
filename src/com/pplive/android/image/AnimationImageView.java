package com.pplive.android.image;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class AnimationImageView extends ImageView {
    AnimationDrawable mAnim;

    boolean mAttached;

    public AnimationImageView(Context context) {
        super(context);
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void updateAnim() {
        Drawable drawable = getDrawable();
        if (mAttached && mAnim != null) {
            mAnim.stop();
        }

        if (drawable instanceof LevelListDrawable && drawable.getCurrent() instanceof AnimationDrawable) {
            mAnim = (AnimationDrawable) drawable.getCurrent();
            if (mAttached) {
                mAnim.start();
            }
        } else if (drawable instanceof AnimationDrawable) {
            mAnim = (AnimationDrawable) drawable;
            if (mAttached) {
                mAnim.start();
            }
        } else {
            mAnim = null;
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateAnim();
    }

    @Override
    public void setImageResource(int resid) {
        super.setImageResource(resid);
        updateAnim();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAnim != null) {
            mAnim.start();
        }
        mAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnim != null) {
            mAnim.stop();
        }
        mAttached = false;
    }
}
