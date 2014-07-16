package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pplive.android.pulltorefresh.FallListHelper.LoadListener;
import com.pplive.android.view.TopBarView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.UserAdapter;
import com.pplive.liveplatform.dialog.RefreshDialog;
import com.pplive.liveplatform.task.user.GetFriendsHelper;

public class MyFansActivity extends Activity {

    private TopBarView mTopBarView;

    private PullToRefreshListView mUserContainer;

    private UserAdapter mUserAdapter;

    private GetFriendsHelper mGetFriendsHelper;

    private RefreshDialog mRefreshDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_fans);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUserContainer = (PullToRefreshListView) findViewById(R.id.user_container);
        mUserContainer.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mGetFriendsHelper.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mGetFriendsHelper.append();
            }
        });

        mUserAdapter = new UserAdapter(this);
        mUserContainer.setAdapter(mUserAdapter);
        mGetFriendsHelper = new GetFriendsHelper(this, mUserAdapter);
        mGetFriendsHelper.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {
                mRefreshDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                mRefreshDialog.hide();
                mUserContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mRefreshDialog.hide();
                mUserContainer.onRefreshComplete();
            }

        });

        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String username = getIntent().getStringExtra(Extra.KEY_USERNAME);

        mGetFriendsHelper.loadFans(username);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRefreshDialog.isShowing()) {
            mRefreshDialog.hide();
        }
    }
}
