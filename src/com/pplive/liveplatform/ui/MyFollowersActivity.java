package com.pplive.liveplatform.ui;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.UserAdapter;
import com.pplive.liveplatform.core.user.GetFriendsHelper;
import com.pplive.liveplatform.widget.TopBarView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyFollowersActivity extends Activity {

    private TopBarView mTopBarView;

    private PullToRefreshListView mUserContainer;

    private UserAdapter mUserAdapter;

    private GetFriendsHelper mGetFriendsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_followers);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUserContainer = (PullToRefreshListView) findViewById(R.id.user_container);

        mUserAdapter = new UserAdapter(this);
        mUserContainer.setAdapter(mUserAdapter);
        mGetFriendsHelper = new GetFriendsHelper(this, mUserAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String username = getIntent().getStringExtra(Extra.KEY_USERNAME);

        mGetFriendsHelper.loadFollowers(username);
    }
}
