package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.SearchService.LiveStatusKeyword;
import com.pplive.liveplatform.core.service.live.SearchService.SortKeyword;
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
import com.pplive.liveplatform.ui.widget.SearchBar;
import com.pplive.liveplatform.ui.widget.refresh.RefreshGridView;

public class SearchActivity extends Activity implements SearchBar.Callback {
    static final String TAG = "_SearchActivity";

    private final static int REFRESH = 1000;

    private final static int APPEND = 1001;

    private final static int FALL_COUNT = 16;

    private SearchBar mSearchBar;

    private String mNextToken;

    private String mKeyword;

    private LiveStatusKeyword mLiveStatus;

    private ProgramContainer mContainer;

    private View mRetryLayout;

    private View mShadowView;

    private TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        mSearchBar = (SearchBar) findViewById(R.id.searchbar_search);
        mSearchBar.setOnClickListener(onSearchBarClickListener);
        mSearchBar.setCallbackListener(this);

        mContainer = (ProgramContainer) findViewById(R.id.layout_search_body);
        mContainer.setOnUpdateListener(onUpdateListener);
        mContainer.setOnStatusChangeListener(onStatusChangeListener);
        mContainer.setPullable(false);
        mRetryLayout = findViewById(R.id.layout_search_nodata);
        mResultText = (TextView) findViewById(R.id.text_search_result);

        mLiveStatus = LiveStatusKeyword.LIVING;

        mShadowView = findViewById(R.id.layout_search_shadow);
        mShadowView.setOnTouchListener(onTouchListener);
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
                mSearchBar.clearFocus();
                startSearchTask(mSearchBar.getText().toString());
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

    private void switchLiveStatus(LiveStatusKeyword id) {
        mLiveStatus = id;
        startSearchTask(mKeyword);
    }

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mResultText.setVisibility(View.VISIBLE);
            mResultText.setText(Html.fromHtml(String.format("<b><font color=white>\"%s\"</font></b>&nbsp;<small><font color=#BBBBBB>搜索结果</font><small>",
                    mKeyword)));
            FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_RESULT);
            mNextToken = fallList.nextToken();
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
                switchLiveStatus(LiveStatusKeyword.LIVING);
                break;
            case R.id.btn_status_tolive:
                switchLiveStatus(LiveStatusKeyword.COMING);
                break;
            case R.id.btn_status_replay:
                switchLiveStatus(LiveStatusKeyword.VOD);
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

    @Override
    public void onShowRecord(boolean show) {
        if (show) {
            mShadowView.setVisibility(View.VISIBLE);
        } else {
            mShadowView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        ImageLoader.getInstance().clearMemoryCache();
        super.onStop();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "onTouch");
                mSearchBar.clearFocus();
            }
            return false;
        }
    };

}
