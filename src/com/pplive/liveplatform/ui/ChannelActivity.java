package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pplive.android.pulltorefresh.FallListHelper.LoadListener;
import com.pplive.android.view.TopBarView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.ProgramAdapter;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.api.live.model.Subject;
import com.pplive.liveplatform.core.api.live.model.Tag;
import com.pplive.liveplatform.dialog.RefreshDialog;
import com.pplive.liveplatform.task.search.SearchProgramHelper;

public class ChannelActivity extends Activity {

    static final String TAG = ChannelActivity.class.getSimpleName();

    private TopBarView mTopBarView;

    private PullToRefreshGridView mContainer;

    private ProgramAdapter mAdapter;

    private SearchProgramHelper mProgramLoader;

    private boolean mInit;

    private RefreshDialog mRefreshDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContainer = (PullToRefreshGridView) findViewById(R.id.program_container);
        mContainer.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                Log.d(TAG, "onPullDownToRefresh");

                mProgramLoader.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                Log.d(TAG, "onPullUpToRefresh");

                mProgramLoader.append();
            }
        });

        mAdapter = new ProgramAdapter(getApplicationContext());
        mContainer.setAdapter(mAdapter);

        mProgramLoader = new SearchProgramHelper(this, mAdapter);
        mProgramLoader.setLoadListener(new LoadListener() {

            @Override
            public void onLoadStart() {
                mRefreshDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                mRefreshDialog.hide();
                mContainer.onRefreshComplete();
            }

            @Override
            public void onLoadFailed() {
                mRefreshDialog.hide();
                mContainer.onRefreshComplete();
            }
        });

        mContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mAdapter) {
                    Program program = mAdapter.getItem(position);

                    Intent intent = new Intent(getApplicationContext(), LivePlayerActivity.class);
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    startActivity(intent);
                }
            }
        });

        GridLayoutAnimationController glac = (GridLayoutAnimationController) AnimationUtils.loadLayoutAnimation(getApplicationContext(),
                R.anim.home_gridview_flyin);
        mContainer.getRefreshableView().setLayoutAnimation(glac);

        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        Intent intent = getIntent();

        Subject subject = (Subject) intent.getSerializableExtra(Extra.KEY_SUBJECT);
        Tag tag = (Tag) intent.getSerializableExtra(Extra.KEY_TAG);

        if (null != subject) {
            updateSubject(subject);
        }

        if (null != tag) {
            updateTag(tag);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mRefreshDialog.isShowing()) {
            mRefreshDialog.dismiss();
        }
    }

    private void updateSubject(Subject subject) {
        if (null != subject) {

            mTopBarView.setTitle(subject.getSubjectName());
        }

        if (!mInit) {
            mInit = true;

            mProgramLoader.searchBySubjectId(subject.getId());
        }
    }

    private void updateTag(Tag tag) {
        if (null != tag) {

            mTopBarView.setTitle(tag.getTagName());
        }

        if (!mInit) {
            mInit = true;

            mProgramLoader.searchByTag(tag.getTagName());
        }
    }

}
