package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.pplive.android.pulltorefresh.FallListHelper.LoadListener;
import com.pplive.android.pulltorefresh.PullToRefreshSwipeListView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.NotivicationAdapter;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.task.user.GetNotificatioinHelper;
import com.pplive.liveplatform.widget.TopBarView;

public class MyNotificationActivity extends Activity {

    private TopBarView mTopBarView;

    private PullToRefreshSwipeListView mNotificationContainer;

    private NotivicationAdapter mAdapter;

    private GetNotificatioinHelper mGetNotificatioinHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_notification);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNotificationContainer = (PullToRefreshSwipeListView) findViewById(R.id.notification_container);
        mNotificationContainer.setOnRefreshListener(new OnRefreshListener2<SwipeListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<SwipeListView> refreshView) {
                mGetNotificatioinHelper.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<SwipeListView> refreshView) {
                mGetNotificatioinHelper.append();
            }

        });

        mAdapter = new NotivicationAdapter(this);
        mNotificationContainer.setAdapter(mAdapter);
        mGetNotificatioinHelper = new GetNotificatioinHelper(this, mAdapter);
        mGetNotificatioinHelper.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {

            }

            @Override
            public void onLoadSucceed() {
                mNotificationContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mNotificationContainer.onRefreshComplete();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        UserManager manager = UserManager.getInstance(this);

        String username = manager.getUsernamePlain();
        String coToken = manager.getToken();
        mGetNotificatioinHelper.getFeeds(coToken, username);
    }
}
