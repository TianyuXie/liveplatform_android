package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class AdvancedGridView extends GridView {

    public AdvancedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedGridView(Context context) {
        super(context);
    }

    private OnReachBottomListener mOnReachBottomListener;

    public void setOnReachBottomListener(OnReachBottomListener l) {
        this.mOnReachBottomListener = l;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount() - 1);
        if (view != null) {
            int diff = (view.getBottom() - (getHeight() + getScrollY()));
            if (diff <= 0) {
                if (mOnReachBottomListener != null) {
                    mOnReachBottomListener.onReachBottom();
                }
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnReachBottomListener {
        public void onReachBottom();
    }
}
