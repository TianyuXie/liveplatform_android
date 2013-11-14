package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;

public class RefreshGridView extends GridView implements AbsListView.OnScrollListener {
    final static String TAG = "RefreshGridView";

    public RefreshGridView(Context context) {
        this(context, null);
    }

    public RefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "firstVisibleItem: " + firstVisibleItem);
        Log.d(TAG, "visibleItemCount: " + visibleItemCount);
        Log.d(TAG, "totalItemCount: " + totalItemCount);
    }

}
