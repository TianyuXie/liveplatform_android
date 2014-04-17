package com.pplive.liveplatform.ui.home;

import java.lang.ref.WeakReference;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import com.pplive.liveplatform.ui.SearchActivity;
import com.pplive.liveplatform.ui.widget.intercept.InterceptDetector;
import com.pplive.liveplatform.ui.widget.intercept.InterceptableRelativeLayout;
import com.pplive.liveplatform.ui.widget.refresh.RefreshGridView;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;
import com.pplive.liveplatform.util.ViewUtil;

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

    //    private TitleBar mTitleBar;

    private ToggleButton mMenuButton;

    private ProgramContainer mContainer;

    private TextView mCatalogTextView;

    private View mRetryLayout;

    private TextView mRetryText;

    private Callback mCallbackListener;

    private boolean mSlided;

    private boolean mBusy;

    private boolean mInit;

    private String mNextToken;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

    private int mSubjectId;

    private LiveStatusKeyword mLiveStatus;

    private Handler mPullHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mPullHandler = new PullHandler(this);
        mSubjectId = CATALOG_ORIGIN;
        mLiveStatus = LiveStatusKeyword.LIVING;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        InterceptableRelativeLayout layout = (InterceptableRelativeLayout) inflater.inflate(R.layout.fragment_home, container, false);
        mContainer = (ProgramContainer) layout.findViewById(R.id.layout_home_body);
        mContainer.setOnUpdateListener(onUpdateListener);
        mContainer.setOnStatusChangeListener(onStatusChangeListener);
        //        mTitleBar = (TitleBar) layout.findViewById(R.id.titlebar_home);
        //        mTitleBar.setOnClickListener(onTitleBarClickListener);
        mCatalogTextView = (TextView) layout.findViewById(R.id.text_home_catalog);
        mRetryText = (TextView) layout.findViewById(R.id.text_home_retry);
        mRetryLayout = layout.findViewById(R.id.layout_home_retry);
        mMenuButton = (ToggleButton) layout.findViewById(R.id.btn_home_menu);
        mMenuButton.setOnClickListener(onMenuClickListener);
        layout.findViewById(R.id.btn_home_search).setOnClickListener(onSearchClickListener);
        layout.findViewById(R.id.btn_home_retry).setOnClickListener(onRetryClickListener);

        layout.setInterceptDetector(new InterceptDetector(getActivity(), onGestureListener));
        updateCatalogText();
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
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
            mContainer.setStatusVisibility(View.GONE);
            if (mContainer.getCheckedRadioButtonId() == R.id.btn_status_living) {
                startRefreshTask();
            } else {
                mContainer.checkStatus(R.id.btn_status_living);
            }
            break;
        }
    }

    private void switchLiveStatus(LiveStatusKeyword id) {
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
                    if (subject == mSubjectId && status == mLiveStatus) {
                        mContainer.refreshData(fallList.getList(), true);
                        if (event.getContext().get(SearchTask.KEY_LIVE_STATUS) == LiveStatusKeyword.COMING) {
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
                                mRetryText.setText(R.string.home_nodata_text);
                                ViewUtil.showLayoutDelay(mRetryLayout, 150);
                            }
                        }
                    } else {
                        Log.d(TAG, "retry");
                        startRefreshTask();
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
                mContainer.clearData();
                mBusy = false;
                mRetryText.setText(R.string.home_fail_text);
                ViewUtil.showLayoutDelay(mRetryLayout, 150);
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
            }
        }
    };

    @Override
    public void onSlide() {
        mSlided = true;
        mMenuButton.setChecked(true);
        mContainer.setItemClickable(false);
    }

    @Override
    public void onSlideBack() {
        mSlided = false;
        mMenuButton.setChecked(false);
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
                switchLiveStatus(LiveStatusKeyword.LIVING);
                break;
            //            case R.id.btn_status_tolive:
            //                switchLiveStatus(LiveStatusKeyword.COMING);
            //                break;
            case R.id.btn_status_replay:
                switchLiveStatus(LiveStatusKeyword.VOD);
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

    private View.OnClickListener onMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallbackListener != null) {
                mCallbackListener.doSlide();
            }
        }
    };

    private View.OnClickListener onSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        }
    };

    static class PullHandler extends Handler {
        private WeakReference<HomeFragment> mOuter;

        public PullHandler(HomeFragment outer) {
            mOuter = new WeakReference<HomeFragment>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeFragment outer = mOuter.get();
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
