package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class DetectableRelativeLayout extends RelativeLayout {
    static final String TAG = "DetectableRelativeLayout";

    private OnSoftInputListener mOnSoftInputListener;

    private boolean mSoftInputShow;

    private int mFullWidth;

    private int mFullHeight;

    private int mHalfHeight;

    public DetectableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetectableRelativeLayout(Context context) {
        this(context, null);
    }

    public int getHalfHeight() {
        return mHalfHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, String.format("onSizeChanged: %d, %d, %d, %d", w, h, oldw, oldh));
        if (oldw == 0 && oldh == 0) {
            mFullWidth = Math.min(w, h);
            mFullHeight = Math.max(w, h);
        } else if (oldw == mFullWidth && oldh == mFullHeight && w == oldw && h < oldh) {
            mHalfHeight = h;
        }

        if (w == oldw && h == mHalfHeight && oldh == mFullHeight) {
            if (!mSoftInputShow) {
                mSoftInputShow = true;
                if (mOnSoftInputListener != null) {
                    mOnSoftInputListener.onSoftInputShow();
                }
            }
        } else if (w == oldw && h == mFullHeight && oldh == mHalfHeight) {
            if (mSoftInputShow) {
                mSoftInputShow = false;
                if (mOnSoftInputListener != null) {
                    mOnSoftInputListener.onSoftInputHide();
                }
            }
        } else if (w > oldw && w > h && oldh == mHalfHeight) {
            if (mSoftInputShow) {
                mSoftInputShow = false;
                if (mOnSoftInputListener != null) {
                    mOnSoftInputListener.onSoftInputHide();
                }
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public interface OnSoftInputListener {
        void onSoftInputShow();

        void onSoftInputHide();
    }

    public void setOnSoftInputListener(OnSoftInputListener l) {
        this.mOnSoftInputListener = l;
    }
}
