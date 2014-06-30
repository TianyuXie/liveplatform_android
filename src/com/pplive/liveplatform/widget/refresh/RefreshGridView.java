package com.pplive.liveplatform.widget.refresh;

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
import android.widget.TextView;

import com.pplive.liveplatform.R;

public class RefreshGridView extends GridView implements OnScrollListener {
    static final String TAG = "_RefreshGridView";

    private final static int STATUS_RELEASE_TO_REFRESH = 800;
    private final static int STATUS_PULL_TO_REFRESH = 801;
    private final static int STATUS_REFRESHING = 802;
    private final static int STATUS_DONE = 803;

    private final static float RATIO = 2.0f;
    private final static float OVER_MULT = 1.2f;
    private final static int REVERSE_LIMIT = 5;
    private final static float MAX_STEP = 100.0f;
    private final static float MOVE_THRESHOLD = 5.0f;
    private final static float REVERSE_THRESHOLD = 20.0f;

    private final static int POPUP_TIME = 500;

    private View mHeaderView;
    private TextView mInfoText;
    //private TextView mTimeText;
    private LinearLayout mPullView;
    private ProgressBar mProgressBar;

    //private long mLastUpdateTime;

    private int mStatus;
    private int mHeaderHeight;
    private float mStartY;

    private float mSavedDelta;
    private int mReverseCount;
    private boolean mDown;

    private boolean mRecorded;
    private boolean mRefreshable;
    private boolean mPullable;
    private boolean mPulling;
    private boolean mScrollDown;
    private boolean mAniming;

    private boolean mSeeLast;
    private boolean mSeeFirst;
    private boolean mReachBottom;
    private boolean mReachTop;

    private OnUpdateListener mUpdateListener;

    public RefreshGridView(Context context) {
        this(context, null);
    }

    public RefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(getContext(), onGestureListener);
        mStatus = STATUS_DONE;
        mPullable = true;
        setOnScrollListener(this);
        LayoutInflater inflater = LayoutInflater.from(context);
        mPullView = (LinearLayout) inflater.inflate(R.layout.layout_home_pull_header, null);
        mProgressBar = (ProgressBar) mPullView.findViewById(R.id.progress_header);
        mInfoText = (TextView) mPullView.findViewById(R.id.text_header_pullinfo);
        //        mTimeText = (TextView) mPullView.findViewById(R.id.text_header_refreshtime);
        measureView(mPullView);
        mHeaderHeight = mPullView.getMeasuredHeight();
        updatePadding(-mHeaderHeight);
        mPullView.invalidate();
    }

    public View getPullView() {
        return mPullView;
    }

    private void updatePadding(float height) {
        mPullView.setPadding(0, (int) (height * 7.0 / 10.0), 0, (int) (height * 3.0 / 10.0));
    }

    public void setHeader(View v) {
        mHeaderView = v;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            Log.d(TAG, "SCROLL_STATE_IDLE");
            if (mScrollDown) {
                mScrollDown = false;
                if (mUpdateListener != null) {
                    mUpdateListener.onScrollDown(false);
                }
            }
            break;
        default:
            break;
        }

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v(TAG, "onInterceptTouchEvent");
        if (mRefreshable && mPulling) {
            Log.v(TAG, "true");
            return true;
        } else {
            Log.v(TAG, "false");
            return super.onInterceptTouchEvent(ev);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");
        mGestureDetector.onTouchEvent(event);

        if (mRefreshable && mPulling && !mAniming) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mRecorded) {
                    mRecorded = true;
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
                        bounceHeader(-mPullView.getHeight());
                        Log.v(TAG, "由下拉刷新状态，到done状态");
                    }

                    if (mStatus == STATUS_RELEASE_TO_REFRESH) {
                        mStatus = STATUS_REFRESHING;
                        updateHeader();
                        bounceHeader(mHeaderHeight - mPullView.getHeight());
                        if (mUpdateListener != null) {
                            mUpdateListener.onRefresh();
                        }
                        Log.v(TAG, "由松开刷新状态，到done状态");
                    }
                }
                mRecorded = false;
                mPulling = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float tempY = event.getY();
                if (!mRecorded) {
                    mRecorded = true;
                    mStartY = tempY;
                    mSavedDelta = 0;
                    mReverseCount = 0;
                    mDown = true;
                }
                if (mStatus != STATUS_REFRESHING && mRecorded) {
                    float delta = tempY - mStartY;
                    delta = Math.min(mSavedDelta + MAX_STEP, delta);
                    delta = Math.max(mSavedDelta - MAX_STEP, delta);
                    if (((delta > mSavedDelta && mDown) || (delta < mSavedDelta && !mDown)) && Math.abs(mSavedDelta - delta) > MOVE_THRESHOLD) {
                        mReverseCount = 0;
                        mSavedDelta = delta;
                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                        // 可以松手去刷新了
                        if (mStatus == STATUS_RELEASE_TO_REFRESH) {
                            setSelection(0);
                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if ((mSavedDelta / RATIO < OVER_MULT * mHeaderHeight) && mSavedDelta > 0) {
                                mStatus = STATUS_PULL_TO_REFRESH;
                                updateHeader();
                                Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
                            }
                            // 一下子推到顶了
                            else if (mSavedDelta <= 0) {
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
                            if (mSavedDelta / RATIO >= OVER_MULT * mHeaderHeight) {
                                mStatus = STATUS_RELEASE_TO_REFRESH;
                                updateHeader();
                                Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
                            }
                            // 上推到顶了
                            else if (mSavedDelta <= 0) {
                                mStatus = STATUS_DONE;
                                mAniming = false;
                                updateHeader();
                                Log.v(TAG, "由Done或者下拉刷新状态转变到done状态");
                            }
                        }
                        // done状态下
                        if (mStatus == STATUS_DONE && mSavedDelta > 0) {
                            mStatus = STATUS_PULL_TO_REFRESH;
                            updateHeader();
                        }
                        // 更新headView的size
                        if (mStatus == STATUS_PULL_TO_REFRESH || mStatus == STATUS_RELEASE_TO_REFRESH) {
                            updatePadding((mSavedDelta / RATIO - mHeaderHeight));
                        }
                    } else if (delta != mSavedDelta && Math.abs(mSavedDelta - delta) > REVERSE_THRESHOLD) {
                        mReverseCount++;
                        if (mReverseCount >= REVERSE_LIMIT) {
                            Log.d(TAG, "Reverse");
                            mReverseCount = 0;
                            mDown = !mDown;
                        }
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
            mInfoText.setText(R.string.refresh_release);
            break;
        case STATUS_PULL_TO_REFRESH:
            Log.d(TAG, "STATUS_PULL_TO_REFRESH");
            mProgressBar.setVisibility(View.INVISIBLE);
            mInfoText.setVisibility(View.VISIBLE);
            //mTimeText.setVisibility(View.VISIBLE);
            //mTimeText.setText(String.format(getContext().getString(R.string.refresh_last_update), TimeUtil.stamp2String(mLastUpdateTime)));

            mInfoText.setText(R.string.refresh_pull);
            break;
        case STATUS_REFRESHING:
            Log.d(TAG, "STATUS_REFRESHING");
            mProgressBar.setVisibility(View.VISIBLE);
            mInfoText.setVisibility(View.GONE);
            //mTimeText.setVisibility(View.GONE);
            break;
        case STATUS_DONE:
            Log.v(TAG, "STATUS_DONE");
            mProgressBar.setVisibility(View.INVISIBLE);
            mInfoText.setVisibility(View.GONE);
            //mTimeText.setVisibility(View.GONE);
            break;
        }
    }

    public void setOnUpdateListener(OnUpdateListener updateListener) {
        this.mUpdateListener = updateListener;
        mRefreshable = true;
    }

    public interface OnUpdateListener {
        public void onRefresh();

        public void onAppend();

        public void onScrollDown(boolean isDown);
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
            if (!mPulling && !mReachBottom) {
                if (distanceY > 0) {
                    if (!mScrollDown) {
                        mScrollDown = true;
                        if (mUpdateListener != null) {
                            mUpdateListener.onScrollDown(true);
                        }
                    }
                } else if (distanceY < 0) {
                    if (mScrollDown) {
                        mScrollDown = false;
                        if (mUpdateListener != null) {
                            mUpdateListener.onScrollDown(false);
                        }
                    }
                }
            }
            float absDistanceX = Math.abs(distanceX);
            float absDistanceY = Math.abs(distanceY);
            if (absDistanceY > absDistanceX) {
                if (distanceY > 10.0f && mReachBottom && !mPulling) {
                    if (mUpdateListener != null) {
                        mUpdateListener.onAppend();
                    }
                } else if (distanceY < -10.0f && mReachTop && mPullable) {
                    mPulling = true;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    public void setPullable(boolean enabled) {
        this.mPullable = enabled;
    }

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

    public void setLastUpdateTime(long time) {
        //this.mLastUpdateTime = time;
    }

    private void bounceHeader(int yTranslate) {
        mAniming = true;
        if (mStatus == STATUS_REFRESHING) {
            updatePadding(0);
        } else if (mStatus == STATUS_DONE) {
            updatePadding(-mHeaderHeight);
        }
        TranslateAnimation bodyAnim = new TranslateAnimation(0, 0, -yTranslate, 0);
        bodyAnim.setDuration(POPUP_TIME);
        bodyAnim.setInterpolator(new OvershootInterpolator(1.2f));

        TranslateAnimation headAnim = new TranslateAnimation(0, 0, -yTranslate, 0);
        headAnim.setDuration(POPUP_TIME);
        headAnim.setInterpolator(new OvershootInterpolator(1.2f));

        TranslateAnimation pullAnim = new TranslateAnimation(0, 0, -yTranslate, 0);
        pullAnim.setDuration(POPUP_TIME);
        pullAnim.setInterpolator(new OvershootInterpolator(1.2f));

        startAnimation(bodyAnim);
        mPullView.startAnimation(pullAnim);
        if (mHeaderView != null && mHeaderView.getVisibility() == VISIBLE) {
            mHeaderView.startAnimation(headAnim);
        }
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        mAniming = false;
        clearAnimation();
        mPullView.clearAnimation();
        if (mHeaderView != null && mHeaderView.getVisibility() == VISIBLE) {
            mHeaderView.clearAnimation();
        }
    }

    public boolean canClick() {
        return (mStatus == STATUS_DONE) && !mAniming && !mPulling;
    }
}