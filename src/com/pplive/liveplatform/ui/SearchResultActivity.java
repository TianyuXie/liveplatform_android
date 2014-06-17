package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.SearchService.LiveStatusKeyword;
import com.pplive.liveplatform.core.service.live.SearchService.SortKeyword;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.home.SearchTask;
import com.pplive.liveplatform.ui.home.ProgramContainer;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.pplive.liveplatform.ui.widget.refresh.RefreshGridView;

public class SearchResultActivity extends Activity {

    static final String TAG = SearchResultActivity.class.getSimpleName();

    public final static String KEY_SEARCH_KEY_WORD = "search_key_word";

    private final static int REFRESH = 1000;

    private final static int APPEND = 1001;

    private final static int FALL_COUNT = 16;

    private TopBarView mTopBarView;

    private String mNextToken;

    private String mKeyword;

    private LiveStatusKeyword mLiveStatus;

    private ProgramContainer mContainer;

    private View mRetryLayout;

    private boolean mBusy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_result);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopBarView.showLeftBtn();

        mContainer = (ProgramContainer) findViewById(R.id.layout_search_body);
        mContainer.setOnUpdateListener(mOnUpdateListener);
        mContainer.setOnStatusChangeListener(mOnStatusChangeListener);
        mContainer.setPullable(false);

        mRetryLayout = findViewById(R.id.layout_search_nodata);

        mLiveStatus = LiveStatusKeyword.LIVING;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String keyword = intent.getStringExtra(KEY_SEARCH_KEY_WORD);

        if (!TextUtils.isEmpty(keyword)) {
            startSearchTask(keyword);
        }

        mTopBarView
                .setTitle(Html.fromHtml(String.format("<b><font color=white>\"%s\"</font></b>&nbsp;<small><font color=#BBBBBB>搜索结果</font><small>", mKeyword)));
    }

    private void startSearchTask(String keyword) {
        if (!mBusy) {
            if (!TextUtils.isEmpty(keyword)) {
                Log.d(TAG, "SearchTask");
                mKeyword = keyword;
                mNextToken = "";
                startTask(keyword, REFRESH);
            }
        }
    }

    public void startAppendTask() {
        if (!mBusy) {
            startTask(mKeyword, APPEND);
        }
    }

    private void startTask(String keyword, int type) {
        if (!mBusy) {
            mBusy = true;
            SearchTask task = new SearchTask();
            task.addTaskListener(onTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(SearchTask.KEY_TYPE, type);
            taskContext.set(SearchTask.KEY_SUBJECT_ID, -1);
            taskContext.set(SearchTask.KEY_NEXT_TK, mNextToken);
            taskContext.set(SearchTask.KEY_KEYWORD, keyword);
            taskContext.set(SearchTask.KEY_LIVE_STATUS, mLiveStatus);
            switch (mLiveStatus) {
            case COMING:
                taskContext.set(SearchTask.KEY_SORT, SortKeyword.START_TIME);
                break;
            case VOD:
            case LIVING:
            default:
                taskContext.set(SearchTask.KEY_SORT, SortKeyword.VV);
                break;
            }
            taskContext.set(SearchTask.KEY_FALL_COUNT, FALL_COUNT);
            task.execute(taskContext);
        }
    }

    private void switchLiveStatus(LiveStatusKeyword id) {
        mLiveStatus = id;
        startSearchTask(mKeyword);
    }

    private Task.TaskListener onTaskListener = new Task.BaseTaskListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            mBusy = false;
            FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_RESULT);
            mNextToken = fallList.nextToken();
            Log.d(TAG, mNextToken);
            LiveStatusKeyword status = (LiveStatusKeyword) event.getContext().get(SearchTask.KEY_LIVE_STATUS);
            int type = (Integer) event.getContext().get(SearchTask.KEY_TYPE);
            switch (type) {
            case REFRESH:
                if (status == mLiveStatus) {
                    mContainer.refreshData(fallList.getList(), false);
                    if (fallList.count() != 0) {
                        mRetryLayout.setVisibility(View.GONE);
                    } else {
                        mRetryLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "retry");
                    startSearchTask(mKeyword);
                }
                break;
            case APPEND:
                mContainer.appendData(fallList.getList());
            default:
                break;
            }
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Log.d(TAG, "SearchTask onTaskFailed: " + event.getMessage());
            mBusy = false;
            Toast.makeText(SearchResultActivity.this, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Task sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            Log.d(TAG, "SearchTask onTimeout");
            mBusy = false;
            Toast.makeText(SearchResultActivity.this, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
            Log.d(TAG, "SearchTask onTaskCancel");
            mBusy = false;
            Toast.makeText(SearchResultActivity.this, R.string.toast_cancel, Toast.LENGTH_SHORT).show();
        }
    };

    private RadioGroup.OnCheckedChangeListener mOnStatusChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
            case R.id.btn_status_living:
                switchLiveStatus(LiveStatusKeyword.LIVING);
                break;
            //            case R.id.btn_status_tolive:
            //                switchLiveStatus(LiveStatusKeyword.COMING);
            //                break;
            case R.id.btn_status_reply:
                switchLiveStatus(LiveStatusKeyword.VOD);
                break;
            default:
                break;
            }
        }
    };

    private RefreshGridView.OnUpdateListener mOnUpdateListener = new RefreshGridView.OnUpdateListener() {
        @Override
        public void onRefresh() {
        }

        @Override
        public void onAppend() {
            Log.d(TAG, "onAppend");
            startAppendTask();
        }

        @Override
        public void onScrollDown(boolean isDown) {
        }
    };

    @Override
    protected void onStop() {
        //ImageLoader.getInstance().clearMemoryCache();
        super.onStop();
    }

}
