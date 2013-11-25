package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.pplive.liveplatform.R;

public class RefreshGridView extends GridView implements OnScrollListener {
    static final String TAG = "PullToRefreshGridView";

    private final static int STATUS_RELEASE_TO_REFRESH = 800;
    private final static int STATUS_PULL_TO_REFRESH = 801;
    private final static int STATUS_REFRESHING = 802;
    private final static int STATUS_DONE = 803;

    private final static float RATIO = 3.0f;

    private LinearLayout mHeaderView;
    private ProgressBar mProgressBar;

    private int mStatus;
    private int mHeaderHeight;
    private float mStartY;

    private boolean mRecored;
    private boolean mRefreshable;
    private boolean mPulling;
    private boolean mAniming;

    private boolean mSeeLast;
    private boolean mSeeFirst;
    private boolean mReachBottom;
    private boolean mReachTop;

    private OnRefreshListener refreshListener;

    public RefreshGridView(Context context) {
        this(context, null);
    }

    public RefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(getContext(), onGestureListener);
        mStatus = STATUS_DONE;
        setOnScrollListener(this);
        LayoutInflater inflater = LayoutInflater.from(context);
        mHeaderView = (LinearLayout) inflater.inflate(R.layout.layout_home_pull_header, null);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.progress_header);
        measureView(mHeaderView);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
        mHeaderView.invalidate();
    }

    public View getHeader() {
        return mHeaderView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d(TAG, "onScrollStateChanged");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "onScroll");
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            mSeeLast = true;
        } else {
            mSeeLast = false;
        }
        if (firstVisibleItem == 0 && totalItemCount > 0) {
            mSeeFirst = true;
        } else {
            mSeeFirst = false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");
        mGestureDetector.onTouchEvent(event);

        if (mRefreshable && mPulling && !mAniming) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mRecored) {
                    mRecored = true;
                    mStartY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mStatus != STATUS_REFRESHING) {
                    if (mStatus == STATUS_DONE) {
                        // 什么都不做
                    }
                    //由下拉刷新状态，到done状态
                    if (mStatus == STATUS_PULL_TO_REFRESH) {
                        mStatus = STATUS_DONE;
                        updateHeader();
                        bounceHeader(-mHeaderView.getHeight());
                        Log.v(TAG, "由下拉刷新状态，到done状态");
                    }

                    if (mStatus == STATUS_RELEASE_TO_REFRESH) {
                        mStatus = STATUS_REFRESHING;
                        updateHeader();
                        bounceHeader(mHeaderHeight - mHeaderView.getHeight());
                        if (refreshListener != null) {
                            refreshListener.onRefresh();
                        }
                        Log.v(TAG, "由松开刷新状态，到done状态");
                    }
                }
                mRecored = false;
                mPulling = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float tempY = event.getY();
                if (!mRecored) {
                    mRecored = true;
                    mStartY = tempY;
                }
                if (mStatus != STATUS_REFRESHING && mRecored) {
                    // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                    // 可以松手去刷新了
                    if (mStatus == STATUS_RELEASE_TO_REFRESH) {
                        setSelection(0);
                        // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                        if (((tempY - mStartY) / RATIO < mHeaderHeight) && (tempY - mStartY) > 0) {
                            mStatus = STATUS_PULL_TO_REFRESH;
                            updateHeader();
                            Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
                        }
                        // 一下子推到顶了
                        else if (tempY - mStartY <= 0) {
                            mStatus = STATUS_DONE;
                            mAniming = false;
                            updateHeader();
                            Log.v(TAG, "由松开刷新状态转变到done状态");
                        }
                        // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                        else {
                            // 不用进行特别的操作，只用更新paddingTop的值就行了
                        }
                    }
                    // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                    if (mStatus == STATUS_PULL_TO_REFRESH) {
                        setSelection(0);
                        // 下拉到可以进入RELEASE_TO_REFRESH的状态
                        if ((tempY - mStartY) / RATIO >= mHeaderHeight) {
                            mStatus = STATUS_RELEASE_TO_REFRESH;
                            updateHeader();
                            Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
                        }
                        // 上推到顶了
                        else if (tempY - mStartY <= 0) {
                            mStatus = STATUS_DONE;
                            mAniming = false;
                            updateHeader();
                            Log.v(TAG, "由Done或者下拉刷新状态转变到done状态");
                        }
                    }
                    // done状态下
                    if (mStatus == STATUS_DONE && tempY - mStartY > 0) {
                        mStatus = STATUS_PULL_TO_REFRESH;
                        updateHeader();
                    }
                    // 更新headView的size
                    if (mStatus == STATUS_PULL_TO_REFRESH || mStatus == STATUS_RELEASE_TO_REFRESH) {
                        mHeaderView.setPadding(0, (int) ((tempY - mStartY) / RATIO - mHeaderHeight), 0, 0);
                    }
                }
                break;
            }
        }
        if (mPulling) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void updateHeader() {
        switch (mStatus) {
        case STATUS_RELEASE_TO_REFRESH:
            Log.d(TAG, "STATUS_RELEASE_TO_REFRESH");
            mProgressBar.setVisibility(View.INVISIBLE);
            break;
        case STATUS_PULL_TO_REFRESH:
            Log.d(TAG, "STATUS_PULL_TO_REFRESH");
            mProgressBar.setVisibility(View.INVISIBLE);
            break;
        case STATUS_REFRESHING:
            Log.d(TAG, "STATUS_REFRESHING");
            mProgressBar.setVisibility(View.VISIBLE);
            break;
        case STATUS_DONE:
            Log.v(TAG, "STATUS_DONE");
            mProgressBar.setVisibility(View.INVISIBLE);
            break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        mRefreshable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public void onRefreshComplete() {
        mStatus = STATUS_DONE;
        updateHeader();
        bounceHeader(-mHeaderHeight);
    }

    // Measure headView's width & height 
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private GestureDetector mGestureDetector;

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float absDistanceX = Math.abs(distanceX);
            float absDistanceY = Math.abs(distanceY);

            if (absDistanceY > absDistanceX) {
                if (distanceY > 10.0f && mReachBottom) {

                } else if (distanceY < -10.0f && mReachTop) {
                    mPulling = true;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View last = getChildAt(getChildCount() - 1);
        if (last != null && (last.getBottom() - (getHeight() + getScrollY())) <= 0 && mSeeLast) {
            mReachBottom = true;
        } else {
            mReachBottom = false;
        }
        View first = getChildAt(0);
        if (first != null && first.getTop() == 0 && mSeeFirst) {
            mReachTop = true;
        } else {
            mReachTop = false;
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void bounceHeader(int yTranslate) {
        mAniming = true;
        if (mStatus == STATUS_REFRESHING) {
            mHeaderView.setPadding(0, 0, 0, 0);
        } else if (mStatus == STATUS_DONE) {
            mHeaderView.setPadding(0, -mHeaderHeight, 0, 0);
        }
        TranslateAnimation bodyAnim = new TranslateAnimation(0, 0, -yTranslate, 0);
        bodyAnim.setDuration(700);
        bodyAnim.setInterpolator(new OvershootInterpolator(1.7f));

        TranslateAnimation headerAnim = new TranslateAnimation(0, 0, -yTranslate, 0);
        headerAnim.setDuration(700);
        headerAnim.setInterpolator(new OvershootInterpolator(1.7f));

        startAnimation(bodyAnim);
        mHeaderView.startAnimation(headerAnim);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        mAniming = false;
        clearAnimation();
        mHeaderView.clearAnimation();
    }

    public boolean isBusy() {
        return mAniming;
    }
}