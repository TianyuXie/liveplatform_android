package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.List;

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
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.SearchService.LiveStatusKeyword;
import com.pplive.liveplatform.core.service.live.SearchService.SortKeyword;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.Subject;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.Task.BaseTaskListener;
import com.pplive.liveplatform.core.task.Task.TaskListener;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.home.SearchTask;
import com.pplive.liveplatform.ui.adpater.ProgramAdapter;
import com.pplive.liveplatform.ui.widget.TopBarView;

public class ChannelActivity extends Activity {

    static final String TAG = ChannelActivity.class.getSimpleName();

    private final static int REFRESH = 1000;

    private final static int APPEND = 1001;

    private final static int FALL_COUNT = 16;

    private TopBarView mTopBarView;

    private PullToRefreshGridView mContainer;

    private ProgramAdapter mAdapter;

    private Subject mSubject;

    private LiveStatusKeyword mLiveStatusKeyword = LiveStatusKeyword.LIVING;

    private SortKeyword mSortKeyword = SortKeyword.ONLINE;

    private String mNextToken;

    private List<Program> mLoadedPrograms = new ArrayList<Program>(FALL_COUNT);

    private boolean mInit;

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

                startRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                Log.d(TAG, "onPullUpToRefresh");

                startAppendTask();
            }
        });

        mAdapter = new ProgramAdapter(getApplicationContext());
        mContainer.setAdapter(mAdapter);

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

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        Intent intent = getIntent();

        Subject subject = (Subject) intent.getSerializableExtra(Extra.KEY_SUBJECT);
        updateSubject(subject);

        if (!mInit) {
            mInit = true;
            startRefreshTask();
        }
    }

    public void updateSubject(Subject subject) {
        if (null != subject) {

            mSubject = subject;
            mTopBarView.setTitle(mSubject.getSubjectName());
        }
    }

    private void startRefreshTask() {
        Log.d(TAG, "refreshTask");

        mLiveStatusKeyword = LiveStatusKeyword.LIVING;
        mSortKeyword = SortKeyword.ONLINE;
        mNextToken = "";

        startLoadTask(REFRESH);
    }

    private void startAppendTask() {
        Log.d(TAG, "appendTask");
        startLoadTask(APPEND);
    }

    private void startAppendTask(int count) {
        Log.d(TAG, "appendTask");
        startLoadTask(APPEND, count);
    }

    private void startLoadTask(int type) {
        startLoadTask(type, FALL_COUNT);
    }

    private void startLoadTask(int type, int count) {
        SearchTask task = new SearchTask();
        task.addTaskListener(mLoadTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(SearchTask.KEY_SUBJECT_ID, mSubject.getId());
        taskContext.set(SearchTask.KEY_TYPE, type);
        taskContext.set(SearchTask.KEY_NEXT_TK, mNextToken);
        taskContext.set(SearchTask.KEY_KEYWORD, "");
        taskContext.set(SearchTask.KEY_LIVE_STATUS, mLiveStatusKeyword);
        taskContext.set(SearchTask.KEY_SORT, mSortKeyword);
        taskContext.set(SearchTask.KEY_FALL_COUNT, count);
        task.execute(taskContext);
    }

    private TaskListener mLoadTaskListener = new BaseTaskListener() {

        public void onTaskFinished(Task sender) {
            mContainer.onRefreshComplete();

        };

        @Override
        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            Log.d(TAG, "SearchTask onTaskFinished");

            FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_RESULT);
            List<Program> tempPrograms = fallList.getList();
            mNextToken = fallList.nextToken();
            mLoadedPrograms.addAll(tempPrograms);

            if (mLoadedPrograms.size() < FALL_COUNT && mLoadedPrograms.size() >= 0 && LiveStatusKeyword.LIVING == mLiveStatusKeyword) {
                mLiveStatusKeyword = LiveStatusKeyword.VOD;
                mSortKeyword = SortKeyword.VV;
                mNextToken = "";

                startAppendTask(FALL_COUNT - tempPrograms.size());
            } else if (mLoadedPrograms.size() >= FALL_COUNT || LiveStatusKeyword.VOD == mLiveStatusKeyword) {
                int type = (Integer) event.getContext().get(SearchTask.KEY_TYPE);

                switch (type) {
                case REFRESH:
                    mAdapter.refreshData(mLoadedPrograms);
                    break;
                case APPEND:
                    mAdapter.appendData(mLoadedPrograms);
                    break;
                default:
                    break;
                }

                mLoadedPrograms.clear();
            }
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Log.d(TAG, "SearchTask onTaskFailed: " + event.getMessage());
        }

    };

}
