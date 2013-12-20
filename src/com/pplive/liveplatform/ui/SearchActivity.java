package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.SearchService;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.home.SearchTask;
import com.pplive.liveplatform.ui.home.ProgramContainer;
import com.pplive.liveplatform.ui.widget.RefreshGridView;
import com.pplive.liveplatform.ui.widget.SearchBar;

public class SearchActivity extends Activity {
    static final String TAG = "_SearchActivity";

    private final static int REFRESH = 1000;

    private final static int APPEND = 1001;

    private final static int FALL_COUNT = 16;

    private SearchBar mSearchBar;

    private String mNextToken;

    private String mKeyword;

    private SearchService.LiveStatusKeyword mLiveStatus;

    private ProgramContainer mContainer;

    private View mRetryLayout;

    private TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        mSearchBar = (SearchBar) findViewById(R.id.searchbar_search);
        mSearchBar.setOnClickListener(onSearchBarClickListener);

        mContainer = (ProgramContainer) findViewById(R.id.layout_search_body);
        mContainer.setOnUpdateListener(onUpdateListener);
        mContainer.setOnStatusChangeListener(onStatusChangeListener);
        mContainer.setPullable(false);
        mRetryLayout = findViewById(R.id.layout_search_nodata);
        mResultText = (TextView) findViewById(R.id.text_search_result);

        mLiveStatus = SearchService.LiveStatusKeyword.LIVING;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSearchBar.forcusEditText();
    }

    private View.OnClickListener onSearchBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.btn_searchbar_close:
                finish();
                break;
            case R.id.btn_searchbar_search:
                mSearchBar.hideRecordList();
                String keyword = mSearchBar.getText().toString();
                startSearchTask(keyword);
                break;
            }
        }
    };

    private void startSearchTask(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            Log.d(TAG, "SearchTask");
            mKeyword = keyword;
            mNextToken = "";
            startTask(keyword, REFRESH);
        }
    }
    
    public void startAppendTask() {
        startTask(mKeyword, APPEND);
    }

    private void startTask(String keyword, int type) {
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
            taskContext.set(SearchTask.KEY_SORT, SearchService.SortKeyword.START_TIME);
            break;
        case VOD:
        case LIVING:
        default:
            taskContext.set(SearchTask.KEY_SORT, SearchService.SortKeyword.VV);
            break;
        }
        taskContext.set(SearchTask.KEY_FALL_COUNT, FALL_COUNT);
        task.execute(taskContext);
    }

    private void switchLiveStatus(SearchService.LiveStatusKeyword id) {
        mLiveStatus = id;
        startSearchTask(mKeyword);
    }

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mResultText.setVisibility(View.VISIBLE);
            mResultText.setText(Html.fromHtml(String.format("<b><font color=white>\"%s\"</font></b>&nbsp;<small><font color=#BBBBBB>搜索结果</font><small>", mKeyword)));
            FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_RESULT);
            mNextToken = fallList.nextToken();
            int type = (Integer) event.getContext().get(SearchTask.KEY_TYPE);
            switch (type) {
            case REFRESH:
                mContainer.refreshData(fallList.getList());
                if (fallList.count() != 0) {
                    mRetryLayout.setVisibility(View.GONE);
                } else {
                    mRetryLayout.setVisibility(View.VISIBLE);
                }
                break;
            case APPEND:
                mContainer.appendData(fallList.getList());
            default:
                break;
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "SearchTask onTaskFailed: " + event.getMessage());
            Toast.makeText(SearchActivity.this, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "SearchTask onTimeout");
            Toast.makeText(SearchActivity.this, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "SearchTask onTaskCancel");
            Toast.makeText(SearchActivity.this, R.string.toast_cancel, Toast.LENGTH_SHORT).show();
        }
    };

    private RadioGroup.OnCheckedChangeListener onStatusChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
            case R.id.btn_status_living:
                switchLiveStatus(SearchService.LiveStatusKeyword.LIVING);
                break;
            case R.id.btn_status_tolive:
                switchLiveStatus(SearchService.LiveStatusKeyword.COMING);
                break;
            case R.id.btn_status_replay:
                switchLiveStatus(SearchService.LiveStatusKeyword.VOD);
                break;
            default:
                break;
            }
        }
    };
    
    private RefreshGridView.OnUpdateListener onUpdateListener = new RefreshGridView.OnUpdateListener() {
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


}
