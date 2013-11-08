package com.pplive.liveplatform.ui.widget.intercept;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class InterceptDetector extends GestureDetector {

    public InterceptDetector(Context context, OnGestureListener listener) {
        super(context, listener);
    }

    public abstract static class OnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public abstract boolean onDown(MotionEvent e);

        @Override
        public abstract void onShowPress(MotionEvent e);

        @Override
        public abstract boolean onSingleTapUp(MotionEvent e);

        /* Not for interception, cannot be overrided  */
        @Override
        public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public final void onLongPress(MotionEvent e) {

        }

        @Override
        public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
