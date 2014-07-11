package com.pplive.android.pulltorefresh;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.R;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class PullToRefreshSwipeListView extends PullToRefreshAdapterViewBase<SwipeListView> {

    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;

    private FrameLayout mLvFooterLoadingFrame;

    private boolean mListViewExtrasEnabled;

    public PullToRefreshSwipeListView(Context context) {
        super(context);

    }

    public PullToRefreshSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PullToRefreshSwipeListView(Context context, Mode mode) {
        super(context, mode);

    }

    public PullToRefreshSwipeListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);

    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void onRefreshing(final boolean doScroll) {
        /**
         * If we're not showing the Refreshing view, or the list is empty, the the header/footer views won't show so we
         * use the normal method.
         */
        ListAdapter adapter = getRefreshableView().getAdapter();
        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }

        super.onRefreshing(false);

        final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
        final int selection, scrollToY;

        switch (getCurrentMode()) {
        case MANUAL_REFRESH_ONLY:
        case PULL_FROM_END:
            origLoadingView = getFooterLayout();
            listViewLoadingView = mFooterLoadingView;
            oppositeListViewLoadingView = mHeaderLoadingView;
            selection = getRefreshableView().getCount() - 1;
            scrollToY = getScrollY() - getFooterSize();
            break;
        case PULL_FROM_START:
        default:
            origLoadingView = getHeaderLayout();
            listViewLoadingView = mHeaderLoadingView;
            oppositeListViewLoadingView = mFooterLoadingView;
            selection = 0;
            scrollToY = getScrollY() + getHeaderSize();
            break;
        }

        // Hide our original Loading View
        origLoadingView.reset();
        origLoadingView.hideAllViews();

        // Make sure the opposite end is hidden too
        oppositeListViewLoadingView.setVisibility(View.GONE);

        // Show the ListView Loading View and set it to refresh.
        listViewLoadingView.setVisibility(View.VISIBLE);
        listViewLoadingView.refreshing();

        if (doScroll) {
            // We need to disable the automatic visibility changes for now
            disableLoadingLayoutVisibilityChanges();

            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);

            // Make sure the ListView is scrolled to show the loading
            // header/footer
            getRefreshableView().setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }
    }

    @Override
    protected void onReset() {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        if (!mListViewExtrasEnabled) {
            super.onReset();
            return;
        }

        final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode()) {
        case MANUAL_REFRESH_ONLY:
        case PULL_FROM_END:
            originalLoadingLayout = getFooterLayout();
            listViewLoadingLayout = mFooterLoadingView;
            selection = getRefreshableView().getCount() - 1;
            scrollToHeight = getFooterSize();
            scrollLvToEdge = Math.abs(getRefreshableView().getLastVisiblePosition() - selection) <= 1;
            break;
        case PULL_FROM_START:
        default:
            originalLoadingLayout = getHeaderLayout();
            listViewLoadingLayout = mHeaderLoadingView;
            scrollToHeight = -getHeaderSize();
            selection = 0;
            scrollLvToEdge = Math.abs(getRefreshableView().getFirstVisiblePosition() - selection) <= 1;
            break;
        }

        // If the ListView header loading layout is showing, then we need to
        // flip so that the original one is showing instead
        if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

            // Set our Original View to Visible
            originalLoadingLayout.showInvisibleViews();

            // Hide the ListView Header/Footer
            listViewLoadingLayout.setVisibility(View.GONE);

            /**
             * Scroll so the View is at the same Y as the ListView header/footer, but only scroll if: we've pulled to
             * refresh, it's positioned correctly
             */
            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                getRefreshableView().setSelection(selection);
                setHeaderScroll(scrollToHeight);
            }
        }

        // Finally, call up to super
        super.onReset();
    }

    @Override
    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

        if (mListViewExtrasEnabled) {
            final Mode mode = getMode();

            if (includeStart && mode.showHeaderLoadingLayout()) {
                proxy.addLayout(mHeaderLoadingView);
            }
            if (includeEnd && mode.showFooterLoadingLayout()) {
                proxy.addLayout(mFooterLoadingView);
            }
        }

        return proxy;
    }

    protected SwipeListView createSwipeListView(Context context, AttributeSet attrs) {
        final SwipeListView lv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            lv = new InternalSwipeListViewSDK9(context, attrs);
        } else {
            lv = new InternalSwipeListView(context, attrs);
        }
        return lv;
    }

    @Override
    protected SwipeListView createRefreshableView(Context context, AttributeSet attrs) {
        SwipeListView lv = createSwipeListView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    @Override
    protected void handleStyledAttributes(TypedArray a) {
        super.handleStyledAttributes(a);

        mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

        if (mListViewExtrasEnabled) {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL);

            // Create Loading Views ready for use later
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
            mHeaderLoadingView.setVisibility(View.GONE);
            frame.addView(mHeaderLoadingView, lp);
            getRefreshableView().addHeaderView(frame, null, false);

            mLvFooterLoadingFrame = new FrameLayout(getContext());
            mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
            mFooterLoadingView.setVisibility(View.GONE);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

            /**
             * If the value for Scrolling While Refreshing hasn't been explicitly set via XML, enable Scrolling While
             * Refreshing.
             */
            if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }

    public void setSwipeListViewListener(SwipeListViewListener listener) {
        getRefreshableView().setSwipeListViewListener(new PullToRefreshSwipeListViewAdapterWrapper(listener));
    }

    public void openAnimate(int position) {
        getRefreshableView().openAnimate(position + 1);
    }

    public void closeAnimate(int position) {
        getRefreshableView().closeAnimate(position + 1);
    }

    public void closeOpenedItem() {
        getRefreshableView().closeOpenedItems();
    }

    public void dismiss(int position) {
        getRefreshableView().dismiss(position + 1);
    }

    public void dismissSelected() {
        getRefreshableView().dismissSelected();
    }

    public void setOffsetLeft(float offsetLeft) {
        getRefreshableView().setOffsetLeft(offsetLeft);
    }

    public void setOffsetRight(float offsetRight) {
        getRefreshableView().setOffsetRight(offsetRight);
    }

    @TargetApi(9)
    final class InternalSwipeListViewSDK9 extends InternalSwipeListView {

        public InternalSwipeListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                    isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshSwipeListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }
    }

    protected class InternalSwipeListView extends SwipeListView implements EmptyViewMethodAccessor, OnScrollListener {

        private boolean mAddedLvFooter = false;

        private List<OnScrollListener> mOnScrollListeners;

        public InternalSwipeListView(Context context, AttributeSet attrs) {
            super(context, attrs);

        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it when using Header/Footer Views and the
             * list is empty. This masks the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it when using Header/Footer Views and the
             * list is empty. This masks the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshSwipeListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }

        @Override
        public void setOnScrollListener(OnScrollListener listener) {
            super.setOnScrollListener(this);

            if (null == mOnScrollListeners) {
                mOnScrollListeners = new ArrayList<OnScrollListener>();
            }

            mOnScrollListeners.add(listener);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (null == mOnScrollListeners) {
                return;
            }

            for (OnScrollListener listener : mOnScrollListeners) {
                listener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (null == mOnScrollListeners) {
                return;
            }

            for (OnScrollListener listener : mOnScrollListeners) {
                listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }

    private static class PullToRefreshSwipeListViewAdapterWrapper implements SwipeListViewListener {

        private SwipeListViewListener mSwipeListViewListener;

        public PullToRefreshSwipeListViewAdapterWrapper(SwipeListViewListener listener) {
            mSwipeListViewListener = listener;
        }

        @Override
        public void onOpened(int position, boolean toRight) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onOpened(position - 1, toRight);
            }
        }

        @Override
        public void onClosed(int position, boolean fromRight) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onClosed(position - 1, fromRight);
            }
        }

        @Override
        public void onListChanged() {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onListChanged();
            }
        }

        @Override
        public void onMove(int position, float x) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onMove(position - 1, x);
            }
        }

        @Override
        public void onStartOpen(int position, int action, boolean right) {
            Log.d("SwipeListView", "onStartOpen");
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onStartOpen(position - 1, action, right);
            }
        }

        @Override
        public void onStartClose(int position, boolean right) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onStartClose(position - 1, right);
            }
        }

        @Override
        public void onClickFrontView(int position) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onClickFrontView(position - 1);
            }
        }

        @Override
        public void onClickBackView(int position) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onClickBackView(position - 1);
            }
        }

        @Override
        public void onDismiss(int[] reverseSortedPositions) {

            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onDismiss(reverseSortedPositions);
            }
        }

        @Override
        public int onChangeSwipeMode(int position) {
            if (null != mSwipeListViewListener) {
                return mSwipeListViewListener.onChangeSwipeMode(position);
            }

            return 0;
        }

        @Override
        public void onChoiceChanged(int position, boolean selected) {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onChoiceChanged(position, selected);
            }
        }

        @Override
        public void onChoiceStarted() {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onChoiceStarted();
            }
        }

        @Override
        public void onChoiceEnded() {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onChoiceEnded();
            }
        }

        @Override
        public void onFirstListItem() {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onFirstListItem();
            }
        }

        @Override
        public void onLastListItem() {
            if (null != mSwipeListViewListener) {
                mSwipeListViewListener.onLastListItem();
            }
        }

    }
}
