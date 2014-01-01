package com.pplive.liveplatform.ui.home;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.pplive.liveplatform.ui.SearchActivity;
import com.pplive.liveplatform.ui.widget.RefreshGridView;
import com.pplive.liveplatform.ui.widget.TitleBar;
import com.pplive.liveplatform.ui.widget.intercept.InterceptDetector;
import com.pplive.liveplatform.ui.widget.intercept.InterceptableRelativeLayout;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;

public class HomeFragment extends Fragment implements SlidableContainer.OnSlideListener {
    static final String TAG = "_HomeFragment";

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
    
    private TitleBar mTitleBar;

    private ProgramContainer mContainer;

    private TextView mCatalogTextView;

    private View mRetryLayout;

    private Callback mCallbackListener;

    private boolean mSlided;

    private boolean mBusy;

    private boolean mInit;

    private String mNextToken;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

    private int mSubjectId;

    private SearchService.LiveStatusKeyword mLiveStatus;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_PULL_DELAY:
                mRefreshDelayed = true;
                break;
            case MSG_PULL_FINISH:
                mRefreshFinish = true;
                break;
            case MSG_PULL_TIMEOUT:
                mContainer.onRefreshComplete();
                return;
            }
            if (mRefreshDelayed && mRefreshFinish) {
                mHandler.removeMessages(MSG_PULL_TIMEOUT);
                mContainer.onRefreshComplete();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mSubjectId = 1;
        mLiveStatus = SearchService.LiveStatusKeyword.LIVING;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        InterceptableRelativeLayout layout = (InterceptableRelativeLayout) inflater.inflate(R.layout.layout_home_fragment, container, false);
        mContainer = (ProgramContainer) layout.findViewById(R.id.layout_home_body);
        mContainer.setOnUpdateListener(onUpdateListener);
        mContainer.setOnStatusChangeListener(onStatusChangeListener);
        mTitleBar = (TitleBar) layout.findViewById(R.id.titlebar_home);
        mTitleBar.setOnClickListener(onTitleBarClickListener);
        mCatalogTextView = (TextView) layout.findViewById(R.id.text_home_catalog);
        mRetryLayout = layout.findViewById(R.id.layout_home_nodata);
        Button retryButton = (Button) layout.findViewById(R.id.btn_home_retry);
        retryButton.setOnClickListener(onRetryClickListener);
        layout.setInterceptDetector(new InterceptDetector(getActivity(), onGestureListener));
        updateCatalogText();
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (!mInit) {
            Log.d(TAG, "init here");
            mInit = true;
            startRefreshTask();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    public void switchSubject(int id) {
        mSubjectId = id;
        updateCatalogText();
        switch (id) {
        case CATALOG_ORIGIN:
        case CATALOG_SPORT:
            mContainer.setStatusVisibility(View.VISIBLE);
            startRefreshTask();
            break;
        case CATALOG_FINANCE:
        case CATALOG_GAME:
        case CATALOG_TV:
            mContainer.setStatusVisibility(View.INVISIBLE);
            if (mContainer.getCheckedRadioButtonId() == R.id.btn_status_living) {
                startRefreshTask();
            } else {
                mContainer.checkStatus(R.id.btn_status_living);
            }
            break;
        }
    }

    private void switchLiveStatus(SearchService.LiveStatusKeyword id) {
        mLiveStatus = id;
        startRefreshTask();
    }

    private void updateCatalogText() {
        switch (mSubjectId) {
        case CATALOG_ORIGIN:
            mCatalogTextView.setText(R.string.home_catalog_original);
            break;
        case CATALOG_TV:
            mCatalogTextView.setText(R.string.home_catalog_tv);
            break;
        case CATALOG_GAME:
            mCatalogTextView.setText(R.string.home_catalog_game);
            break;
        case CATALOG_SPORT:
            mCatalogTextView.setText(R.string.home_catalog_sport);
            break;
        case CATALOG_FINANCE:
            mCatalogTextView.setText(R.string.home_catalog_finance);
            break;
        }
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
            if (mCallbackListener != null) {
                mCallbackListener.doLoadMore();
            }
        }
    }

    public void startAppendTask() {
        if (!mBusy) {
            Log.d(TAG, "appendTask");
            startTask(APPEND);
            if (mCallbackListener != null) {
                mCallbackListener.doLoadMore();
            }
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
                switch (type) {
                case PULL:
                    mHandler.sendEmptyMessage(MSG_PULL_FINISH);
                    mContainer.refreshData(fallList.getList());
                    break;
                case REFRESH:
                    mContainer.refreshData(fallList.getList());
                    if (event.getContext().get(SearchTask.KEY_LIVE_STATUS) == SearchService.LiveStatusKeyword.COMING) {
                        mContainer.startTimer();
                    } else {
                        mContainer.stopTimer();
                    }
                    if (mCallbackListener != null) {
                        if (fallList.count() != 0) {
                            mCallbackListener.doLoadResult(String.format(Locale.US, getString(R.string.home_loaded_count), fallList.count()));
                            mRetryLayout.setVisibility(View.GONE);
                        } else {
                            mCallbackListener.doLoadResult(getString(R.string.home_nodata_button));
                            mRetryLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case APPEND:
                    mContainer.appendData(fallList.getList());
                    if (mCallbackListener != null) {
                        if (fallList.count() != 0) {
                            mCallbackListener.doLoadResult(String.format(Locale.US, getString(R.string.home_loaded_count), fallList.count()));
                        } else {
                            mCallbackListener.doLoadResult(getString(R.string.home_all_loaded));
                        }
                    }
                    break;
                default:
                    break;
                }
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "SearchTask onTaskFailed: " + event.getMessage());
            if (getActivity() != null) {
                mBusy = false;
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
            }
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "SearchTask onTimeout");
            if (getActivity() != null) {
                mBusy = false;
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
            }
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "SearchTask onTaskCancel");
            if (getActivity() != null) {
                mBusy = false;
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
//                Toast.makeText(getActivity(), R.string.toast_cancel, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onSlide() {
        mSlided = true;
        mTitleBar.setMenuButtonHighlight(true);
        mContainer.setItemClickable(false);
    }

    @Override
    public void onSlideBack() {
        mSlided = false;
        mTitleBar.setMenuButtonHighlight(false);
        mContainer.setItemClickable(true);
    }

    public interface Callback {
        public void doSlide();

        public void doSlideBack();

        public void doLoadMore();

        public void doLoadResult(String text);

        public void doLoadFinish();

        public void doScrollDown(boolean isDown);
    }

    public void setCallbackListener(Callback listener) {
        this.mCallbackListener = listener;
    }

    private View.OnClickListener onTitleBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.btn_titlebar_menu:
                if (mCallbackListener != null) {
                    mCallbackListener.doSlide();
                }
                break;
            case R.id.btn_titlebar_search:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
            }
        }
    };

    private RefreshGridView.OnUpdateListener onUpdateListener = new RefreshGridView.OnUpdateListener() {

        @Override
        public void onRefresh() {
            Log.d(TAG, "onRefresh");
            mRefreshFinish = false;
            mRefreshDelayed = false;
            startPullTask();
            mHandler.sendEmptyMessageDelayed(MSG_PULL_DELAY, 1500);
            mHandler.sendEmptyMessageDelayed(MSG_PULL_TIMEOUT, 10000);
        }

        @Override
        public void onAppend() {
            Log.d(TAG, "onAppend");
            startAppendTask();
        }

        @Override
        public void onScrollDown(boolean isDown) {
            if (!mSlided && mCallbackListener != null) {
                mCallbackListener.doScrollDown(isDown);
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener onStatusChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Log.d(TAG, "onCheckedChanged");
            switch (checkedId) {
            case R.id.btn_status_living:
                switchLiveStatus(SearchService.LiveStatusKeyword.LIVING);
                Log.d(TAG, "btn_status_living");
                break;
            case R.id.btn_status_tolive:
                switchLiveStatus(SearchService.LiveStatusKeyword.COMING);
                Log.d(TAG, "btn_status_tolive");
                break;
            case R.id.btn_status_replay:
                switchLiveStatus(SearchService.LiveStatusKeyword.VOD);
                Log.d(TAG, "btn_status_replay");
                break;
            default:
                break;
            }
        }
    };

    private InterceptDetector.OnGestureListener onGestureListener = new InterceptDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            return mSlided;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            if (mSlided) {
                if (mCallbackListener != null) {
                    mCallbackListener.doSlideBack();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress");
            if (mSlided && mCallbackListener != null) {
                mCallbackListener.doSlideBack();
            }
        }
    };

    private View.OnClickListener onRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startRefreshTask();
        }
    };
}
