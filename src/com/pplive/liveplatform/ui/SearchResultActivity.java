package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.ProgramAdapter;
import com.pplive.liveplatform.core.search.ProgramLoader;
import com.pplive.liveplatform.core.search.ProgramLoader.LoadListener;
import com.pplive.liveplatform.widget.TopBarView;

public class SearchResultActivity extends Activity {

    static final String TAG = SearchResultActivity.class.getSimpleName();

    public final static String KEY_SEARCH_KEY_WORD = "search_key_word";

    private TopBarView mTopBarView;

    private PullToRefreshGridView mContainer;

    private ProgramAdapter mAdapter;

    private ProgramLoader mProgramLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContainer = (PullToRefreshGridView) findViewById(R.id.program_container);
        mContainer.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mProgramLoader.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                mProgramLoader.append();
            }
        });

        mAdapter = new ProgramAdapter(this);
        mContainer.setAdapter(mAdapter);

        mProgramLoader = new ProgramLoader(mAdapter);
        mProgramLoader.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {

            }

            @Override
            public void onLoadSucceed() {
                mContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mContainer.onRefreshComplete();
            }
        });

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getApplicationContext(),
                R.anim.home_gridview_flyin);
        mContainer.getRefreshableView().setLayoutAnimation(glac);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String keyword = intent.getStringExtra(KEY_SEARCH_KEY_WORD);

        mTopBarView.setTitle(getString(R.string.search_program_title_fmt, keyword));

        mProgramLoader.searchByKeyword(keyword);
    }

}
