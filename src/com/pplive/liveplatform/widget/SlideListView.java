package com.pplive.liveplatform.widget;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.pplive.liveplatform.R;

public class SlideListView extends ListView {

    static final String TAG = "_SlideListView";

    private final static int DURATION_STEP = 10;

    private Boolean mIsHorizontal;

    private View mPreItemView;

    private View mCurrentItemView;

    private float mFirstX;

    private float mFirstY;

    private int mRightViewWidth;

    private boolean mIsShown;

    private boolean mSlidable;

    private MoveHandler mHandler = new MoveHandler(this);

    public SlideListView(Context context) {
        this(context, null);
    }

    public SlideListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlideListView);
        for (int i = 0; i < array.getIndexCount(); i++) {
            int attr = array.getIndex(i);
            switch (attr) {
            case R.styleable.SlideListView_extra_width:
                mRightViewWidth = array.getDimensionPixelSize(attr, 0);
                break;
            case R.styleable.SlideListView_slideable:
                mSlidable = array.getBoolean(attr, true);
                break;
            }
        }

        array.recycle();
    }

    /**
     * return true, deliver to listView. return false, deliver to child. if move, return true
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mSlidable) {
            float lastX = ev.getX();
            float lastY = ev.getY();
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsHorizontal = null;
                mFirstX = lastX;
                mFirstY = lastY;
                int motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);

                if (motionPosition >= 0) {
                    View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
                    mPreItemView = mCurrentItemView;
                    mCurrentItemView = currentItemView;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsShown && (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
                    Log.d(TAG, "1---> hiddenRight");
                    /**
                     * 情况一
                     * <p>
                     * 一个Item的右边布局已经显示，
                     * <p>
                     * 这时候点击任意一个item, 那么那个右边布局显示的item隐藏其右边布局
                     */
                    hideRight(mPreItemView);
                }
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isHitCurItemLeft(float x) {
        return x < getWidth() - mRightViewWidth;
    }

    /**
     * @param dx
     * @param dy
     * @return judge if can judge scroll direction
     */
    private boolean judgeScrollDirection(float dx, float dy) {
        boolean canJudge = true;

        if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
            mIsHorizontal = true;
        } else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
            mIsHorizontal = false;
        } else {
            canJudge = false;
        }

        return canJudge;
    }

    /**
     * return false, can't move any direction. return true, cant't move vertical, can move horizontal. return
     * super.onTouchEvent(ev), can move both.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mSlidable) {
            float lastX = ev.getX();
            float lastY = ev.getY();

            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                // confirm is scroll direction
                if (mIsHorizontal == null) {
                    if (!judgeScrollDirection(dx, dy)) {
                        break;
                    }
                }

                if (mIsHorizontal) {
                    if (mIsShown && mPreItemView != mCurrentItemView) {
                        Log.d(TAG, "2---> hiddenRight");
                        /**
                         * 情况二
                         * <p>
                         * 一个Item的右边布局已经显示，
                         * <p>
                         * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
                         * <p>
                         * 向左滑动只触发该情况，向右滑动还会触发情况五
                         */
                        hideRight(mPreItemView);
                    }

                    if (mIsShown && mPreItemView == mCurrentItemView) {
                        dx = dx - mRightViewWidth;
                    }

                    // can't move beyond boundary
                    if (dx < 0 && dx > -mRightViewWidth) {
                        mCurrentItemView.scrollTo((int) (-dx), 0);
                    }

                    return true;
                } else {
                    if (mIsShown) {
                        Log.d(TAG, "3---> hiddenRight");
                        /**
                         * 情况三
                         * <p>
                         * 一个Item的右边布局已经显示，
                         * <p>
                         * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
                         */
                        hideRight(mPreItemView);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPressedState();
                if (mIsShown) {
                    Log.d(TAG, "4---> hiddenRight");
                    /**
                     * 情况四
                     * <p>
                     * 一个Item的右边布局已经显示，
                     * <p>
                     * 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
                     */
                    hideRight(mPreItemView);
                }

                if (mIsHorizontal != null && mIsHorizontal) {
                    if (mFirstX - lastX > mRightViewWidth / 4.0) {
                        showRight(mCurrentItemView);
                    } else {
                        Log.d(TAG, "5---> hiddenRight");
                        /**
                         * 情况五
                         * <p>
                         * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
                         */
                        hideRight(mCurrentItemView);
                    }
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void clearPressedState() {
        mCurrentItemView.setPressed(false);
        setPressed(false);
        refreshDrawableState();
        // invalidate();
    }

    private void showRight(View view) {
        Log.d(TAG, "showRight");
        if (view == null) {
            return;
        }

        Message msg = mHandler.obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = mRightViewWidth;
        msg.sendToTarget();

        mIsShown = true;
    }

    public void hideRight() {
        hideRight(mPreItemView);
    }

    public void hideRight(View view) {
        Log.d(TAG, "hideRight");

        if (mCurrentItemView == null || view == null) {
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = 0;

        msg.sendToTarget();

        mIsShown = false;
    }

    public void setSlidable(boolean sliable) {
        this.mSlidable = sliable;
    }

    /**
     * show or hide right layout animation
     */
    static class MoveHandler extends Handler {
        int mStepX = 0;

        int mFromX;

        int mToX;

        View mView;

        WeakReference<SlideListView> mOuter;

        boolean mIsInAnimation = false;

        public MoveHandler(SlideListView outer) {
            mOuter = new WeakReference<SlideListView>(outer);
        }

        private void animationOver() {
            mIsInAnimation = false;
            mStepX = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SlideListView outer = mOuter.get();
            if (outer == null) {
                return;
            }

            if (mStepX == 0) {
                if (mIsInAnimation) {
                    return;
                }

                mIsInAnimation = true;
                mView = (View) msg.obj;
                mFromX = msg.arg1;
                mToX = msg.arg2;
                mStepX = (int) ((mToX - mFromX) * DURATION_STEP * 1.0 / 100);
                if (mStepX < 0 && mStepX > -1) {
                    mStepX = -1;
                } else if (mStepX > 0 && mStepX < 1) {
                    mStepX = 1;
                }

                if (Math.abs(mToX - mFromX) < 10) {
                    if (mView != null) {
                        mView.scrollTo(mToX, 0);
                    }
                    animationOver();
                    return;
                }
            }

            mFromX += mStepX;
            boolean isLastStep = (mStepX > 0 && mFromX > mToX) || (mStepX < 0 && mFromX < mToX);
            if (isLastStep) {
                mFromX = mToX;
            }
            if (mView != null) {
                mView.scrollTo(mFromX, 0);
            }
            outer.invalidate();

            if (!isLastStep) {
                this.sendEmptyMessageDelayed(0, DURATION_STEP);
            } else {
                animationOver();
            }
        }
    }

}