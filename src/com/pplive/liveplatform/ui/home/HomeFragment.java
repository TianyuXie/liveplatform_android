package com.pplive.liveplatform.ui.home;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.FallList;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.home.SearchTask;
import com.pplive.liveplatform.ui.home.program.ProgramsContainer;
import com.pplive.liveplatform.ui.widget.RefreshGridView;
import com.pplive.liveplatform.ui.widget.SearchBar;
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

    private TitleBar mTitleBar;

    private ProgramsContainer mContainer;

    private SearchBar mSearchBar;

    private Callback mCallbackListener;

    private boolean mSlided;

    private boolean mBusy;

    private boolean mInit;

    private String mNextToken;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        InterceptableRelativeLayout layout = (InterceptableRelativeLayout) inflater.inflate(R.layout.layout_home_fragment, container, false);
        mContainer = (ProgramsContainer) layout.findViewById(R.id.layout_home_body);
        mContainer.setOnUpdateListener(onUpdateListener);
        mTitleBar = (TitleBar) layout.findViewById(R.id.titlebar_home);
        mTitleBar.setOnClickListener(titleBarOnClickListener);
        mSearchBar = (SearchBar) layout.findViewById(R.id.searchbar_home);
        mSearchBar.setOnClickListener(searchBarOnClickListener);
        layout.setInterceptDetector(new InterceptDetector(getActivity(), onGestureListener));
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (!mInit) {
            mInit = true;
            startRefreshTask(1);
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    public void startSearchTask(String keyword) {
        if (!mBusy) {
            Log.d(TAG, "SearchTask");
            mNextToken = "";
            startTask(1, keyword, REFRESH);
        }
    }

    public void startPullTask(int subjectid) {
        if (!mBusy) {
            Log.d(TAG, "pullTask");
            mNextToken = "";
            startTask(subjectid, PULL);
        }
    }

    public void startRefreshTask(int subjectid) {
        if (!mBusy) {
            Log.d(TAG, "refreshTask");
            mNextToken = "";
            startTask(subjectid, REFRESH);
            if (mCallbackListener != null) {
                mCallbackListener.doLoadMore();
            }
        }
    }

    public void startAppendTask(int subjectid) {
        if (!mBusy) {
            Log.d(TAG, "appendTask");
            startTask(subjectid, APPEND);
            if (mCallbackListener != null) {
                mCallbackListener.doLoadMore();
            }
        }
    }

    private void startTask(int subjectid, int type) {
        startTask(subjectid, "", type);
    }

    private void startTask(int subjectid, String keyword, int type) {
        if (!mBusy) {
            mBusy = true;
            SearchTask task = new SearchTask();
            task.addTaskListener(getTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(SearchTask.KEY_SUBJECT_ID, subjectid);
            taskContext.set(SearchTask.KEY_TYPE, type);
            taskContext.set(SearchTask.KEY_NEXT_TK, mNextToken);
            taskContext.set(SearchTask.KEY_KEYWORD, keyword);
            taskContext.set(SearchTask.KEY_LIVE_STATUS, "living");
            taskContext.set(SearchTask.KEY_SORT, "starttime");
            taskContext.set(SearchTask.KEY_FALL_COUNT, 8);
            task.execute(taskContext);
        }
    }

    private Task.OnTaskListener getTaskListener = new Task.OnTaskListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            if (getActivity() != null) {
                mBusy = false;
                FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_TASK_RESULT);
                if (!fallList.nextToken().equals("")) {
                    mNextToken = fallList.nextToken();
                }

                int type = (Integer) event.getContext().get(SearchTask.KEY_TYPE);
                switch (type) {
                case PULL:
                    mHandler.sendEmptyMessage(MSG_PULL_FINISH);
                    mContainer.refreshData(fallList.getList());
                    break;
                case REFRESH:
                    mContainer.refreshData(fallList.getList());
                    if (mCallbackListener != null) {
                        if (fallList.count() != 0) {
                            mCallbackListener.doLoadResult(String.format(Locale.US, "已加载%d条", fallList.count()));
                        } else {
                            mCallbackListener.doLoadResult("暂时没有数据");
                        }
                    }
                    break;
                case APPEND:
                    mContainer.appendData(fallList.getList());
                    if (mCallbackListener != null) {
                        if (fallList.count() != 0) {
                            mCallbackListener.doLoadResult(String.format(Locale.US, "已加载%d条", fallList.count()));
                        } else {
                            mCallbackListener.doLoadResult("已全部加载");
                        }
                    }
                default:
                    break;
                }
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            if (getActivity() != null) {
                mBusy = false;
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
                Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            if (getActivity() != null) {
                mBusy = false;
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
                Toast.makeText(getActivity(), R.string.toast_timeout, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            if (getActivity() != null) {
                mBusy = false;
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
                Toast.makeText(getActivity(), R.string.toast_cancel, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //    public void setIdle() {
    //        mBusy = false;
    //        mContainer.setBusy(false);
    //    }

    //    public boolean isBusy() {
    //        return mBusy;
    //    }

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
    }

    public void setCallbackListener(Callback listener) {
        this.mCallbackListener = listener;
    }

    private View.OnClickListener titleBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.btn_titlebar_menu:
                if (mCallbackListener != null) {
                    mCallbackListener.doSlide();
                }
                break;
            case R.id.btn_titlebar_search:
                mSearchBar.show();
                break;
            default:
                break;
            }
        }
    };

    private View.OnClickListener searchBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick");
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            switch (v.getId()) {
            case R.id.btn_searchbar_close:
                Log.d(TAG, "btn_searchbar_close");
                mSearchBar.hide();
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                break;
            case R.id.btn_searchbar_search:
                Log.d(TAG, "btn_searchbar_search");
                mSearchBar.hide();
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                startSearchTask(mSearchBar.getText());
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
            startPullTask(1);
            mHandler.sendEmptyMessageDelayed(MSG_PULL_DELAY, 2000);
            mHandler.sendEmptyMessageDelayed(MSG_PULL_TIMEOUT, 8000);
        }

        @Override
        public void onAppend() {
            Log.d(TAG, "onAppend");
            startAppendTask(1);
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
}
