package com.pplive.liveplatform.ui.navigate;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.pplive.liveplatform.ui.widget.SearchBarView;
import com.pplive.liveplatform.ui.widget.refresh.RefreshGridView;
import com.pplive.liveplatform.util.ViewUtil;

public class ChannelFragment extends Fragment {

    static final String TAG = ChannelFragment.class.getSimpleName();

    private final static int REFRESH = 1000;

    private final static int APPEND = 1001;

    private final static int PULL = 1002;

    private final static int MSG_PULL_DELAY = 2000;

    private final static int MSG_PULL_FINISH = 2001;

    private final static int MSG_PULL_TIMEOUT = 2002;

    private final static int FALL_COUNT = 16;

    private final static int CATALOG_ORIGIN = 1;

    private final static int CATALOG_TV = 2;

    private final static int CATALOG_GAME = 3;

    private final static int CATALOG_SPORT = 4;

    private final static int CATALOG_FINANCE = 5;

    private SearchBarView mSearchTopBarView;

    private ProgramContainer mContainer;

    private View mRetryLayout;

    private TextView mRetryText;

    private boolean mBusy;

    private boolean mInit;

    private String mNextToken;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

    private int mSubjectId;

    private LiveStatusKeyword mLiveStatus;

    private Handler mPullHandler;

    private boolean mShowStatus = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mPullHandler = new PullHandler(this);
        //        mSubjectId = CATALOG_ORIGIN;
        mLiveStatus = LiveStatusKeyword.LIVING;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(null);

        mContainer.checkStatus(R.id.btn_status_living);

        updateTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_channel, container, false);
        mContainer = (ProgramContainer) layout.findViewById(R.id.layout_channel_body);
        mContainer.setOnUpdateListener(onUpdateListener);
        mContainer.setOnStatusChangeListener(onStatusChangeListener);
        //        mTitleBar = (TitleBar) layout.findViewById(R.id.titlebar_home);
        //        mTitleBar.setOnClickListener(onTitleBarClickListener);

        mSearchTopBarView = (SearchBarView) layout.findViewById(R.id.search_top_bar);

        mRetryText = (TextView) layout.findViewById(R.id.text_channel_retry);
        mRetryLayout = layout.findViewById(R.id.layout_channel_retry);
        layout.findViewById(R.id.btn_channel_retry).setOnClickListener(mOnRetryClickListener);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        mContainer.setStatusVisibility(mShowStatus ? View.VISIBLE : View.GONE);

        Log.d(TAG, "onStart");
        if (!mInit) {
            mInit = true;
            startRefreshTask();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mPullHandler.removeCallbacksAndMessages(null);
        mContainer.stopTimer();
        super.onDestroy();
    }

    public void switchSubject(int id) {
        Log.d(TAG, "switchSubject: " + id);
        switch (id) {
        case CATALOG_ORIGIN:
        case CATALOG_SPORT:
            mShowStatus = true;
            //            mContainer.setStatusVisibility(View.VISIBLE);
            break;
        case CATALOG_FINANCE:
        case CATALOG_GAME:
        case CATALOG_TV:
            mShowStatus = false;
            //            mContainer.setStatusVisibility(View.GONE);

            break;
        }

        mSubjectId = id;
        mLiveStatus = LiveStatusKeyword.LIVING;

        if (null != mContainer) {
            mContainer.checkStatus(R.id.btn_status_living);
            mContainer.setStatusVisibility(mShowStatus ? View.VISIBLE : View.GONE);

            updateTitle();

            startRefreshTask();
        }
    }

    private void updateTitle() {

        String title = null;
        switch (mSubjectId) {
        case CATALOG_ORIGIN:
            title = "原创";
            break;
        case CATALOG_SPORT:
            title = "体育";
            break;
        case CATALOG_FINANCE:
            title = "财经";
            break;
        case CATALOG_GAME:
            title = "游戏";
            break;
        case CATALOG_TV:
            title = "电视台";
            break;
        default:
            title = "爱播";
            break;
        }

        mSearchTopBarView.setTitle(title);
    }

    private void switchLiveStatus(LiveStatusKeyword status) {
        mLiveStatus = status;
        startRefreshTask();
    }

    public void startPullTask() {
        if (!mBusy) {
            Log.d(TAG, "pullTask");
            mNextToken = "";
            startTask(PULL);
        }
    }

    public void startRefreshTask() {
        if (!mBusy) {
            Log.d(TAG, "refreshTask");
            mNextToken = "";
            startTask(REFRESH);
        }
    }

    public void startAppendTask() {
        if (!mBusy) {
            Log.d(TAG, "appendTask");
            startTask(APPEND);
        }
    }

    private void startTask(int type) {
        startTask("", type);
    }

    private void startTask(String keyword, int type) {
        if (!mBusy) {
            mBusy = true;
            SearchTask task = new SearchTask();
            task.addTaskListener(onTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(SearchTask.KEY_SUBJECT_ID, mSubjectId);
            taskContext.set(SearchTask.KEY_TYPE, type);
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

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "SearchTask onTaskFinished");
            if (getActivity() != null) {
                mBusy = false;
                mContainer.setUpdateTime(System.currentTimeMillis());
                FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_RESULT);
                mNextToken = fallList.nextToken();
                int type = (Integer) event.getContext().get(SearchTask.KEY_TYPE);
                int subject = (Integer) event.getContext().get(SearchTask.KEY_SUBJECT_ID);
                LiveStatusKeyword status = (LiveStatusKeyword) event.getContext().get(SearchTask.KEY_LIVE_STATUS);

                Log.d(TAG, "type: " + type);

                switch (type) {
                case PULL:
                    mPullHandler.sendEmptyMessage(MSG_PULL_FINISH);
                    if (subject == mSubjectId && status == mLiveStatus) {
                        mContainer.refreshData(fallList.getList(), false);
                    } else {
                        Log.d(TAG, "retry");
                        startRefreshTask();
                    }
                    break;
                case REFRESH:
                    Log.d(TAG, "subject: " + subject + "; mSubjectId: " + mSubjectId + "; status: " + status + "; mLiveStatus: " + mLiveStatus);

                    if (subject == mSubjectId && status == mLiveStatus) {
                        mContainer.refreshData(fallList.getList(), true);
                        if (event.getContext().get(SearchTask.KEY_LIVE_STATUS) == LiveStatusKeyword.COMING) {
                            mContainer.startTimer();
                        } else {
                            mContainer.stopTimer();
                        }
                        if (fallList.count() != 0) {
                            mRetryLayout.setVisibility(View.GONE);
                        } else {
                            mRetryText.setText(R.string.home_nodata_text);
                            ViewUtil.showLayoutDelay(mRetryLayout, 150);
                        }
                    } else {
                        Log.d(TAG, "retry");
                        startRefreshTask();
                    }
                    break;
                case APPEND:
                    mContainer.appendData(fallList.getList());
                    break;
                }
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "SearchTask onTaskFailed: " + event.getMessage());
            if (getActivity() != null) {
                mContainer.clearData();
                mBusy = false;
                mRetryText.setText(R.string.home_fail_text);
                ViewUtil.showLayoutDelay(mRetryLayout, 150);
            }
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "SearchTask onTimeout");
            if (getActivity() != null) {
                mContainer.clearData();
                mBusy = false;
                mRetryText.setText(R.string.home_fail_text);
                ViewUtil.showLayoutDelay(mRetryLayout, 150);
            }
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "SearchTask onTaskCancel");
            if (getActivity() != null) {
                mBusy = false;
            }
        }
    };

    public interface Callback {
        public void doSlide();

        public void doSlideBack();

        public void doLoadMore();

        public void doLoadResult(String text);

        public void doLoadFinish();

        public void doScrollDown(boolean isDown);
    }

    private RefreshGridView.OnUpdateListener onUpdateListener = new RefreshGridView.OnUpdateListener() {

        @Override
        public void onRefresh() {
            Log.d(TAG, "onRefresh");
            mRefreshFinish = false;
            mRefreshDelayed = false;
            startPullTask();
            mPullHandler.sendEmptyMessageDelayed(MSG_PULL_DELAY, 1500);
            mPullHandler.sendEmptyMessageDelayed(MSG_PULL_TIMEOUT, 10000);
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

    private RadioGroup.OnCheckedChangeListener onStatusChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Log.d(TAG, "onCheckedChanged");
            switch (checkedId) {
            case R.id.btn_status_living:
                Log.d(TAG, "living");
                switchLiveStatus(LiveStatusKeyword.LIVING);
                break;
            case R.id.btn_status_reply:
                Log.d(TAG, "replay");
                switchLiveStatus(LiveStatusKeyword.VOD);
                break;
            default:
                break;
            }
        }
    };

    private View.OnClickListener mOnRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startRefreshTask();
        }
    };

    static class PullHandler extends Handler {
        private WeakReference<ChannelFragment> mOuter;

        public PullHandler(ChannelFragment outer) {
            mOuter = new WeakReference<ChannelFragment>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            ChannelFragment outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                case MSG_PULL_DELAY:
                    outer.mRefreshDelayed = true;
                    break;
                case MSG_PULL_FINISH:
                    outer.mRefreshFinish = true;
                    break;
                case MSG_PULL_TIMEOUT:
                    outer.mContainer.onRefreshComplete();
                    return;
                }
                if (outer.mRefreshDelayed && outer.mRefreshFinish) {
                    removeMessages(MSG_PULL_TIMEOUT);
                    outer.mContainer.onRefreshComplete();
                }
            }
        }
    }
}
