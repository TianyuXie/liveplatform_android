package com.pplive.liveplatform.ui.home;

import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
    static final String TAG = "HomeFragment";

    private TitleBar mTitleBar;

    private ProgramsContainer mContainer;

    private SearchBar mSearchBar;

    private Callback mCallbackListener;

    private boolean mSlided;

    private String mNextToken;

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
        mNextToken = "";
        startTask(1);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void startTask(int subjectid) {
        SearchTask task = new SearchTask();
        task.addTaskListener(getTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(SearchTask.KEY_SUBJECT_ID, subjectid);
        taskContext.set(SearchTask.KEY_NEXT_TK, mNextToken);
        taskContext.set(SearchTask.KEY_LIVE_STATUS, "living");
        taskContext.set(SearchTask.KEY_SORT, "starttime");
        taskContext.set(SearchTask.KEY_FALL_COUNT, 8);
        task.execute(taskContext);
        if (mCallbackListener != null) {
            mCallbackListener.doLoadMore();
        }
    }

    private Task.OnTaskListener getTaskListener = new Task.OnTaskListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.toast_sucess, Toast.LENGTH_SHORT).show();
                FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_TASK_RESULT);
                mNextToken = fallList.nextToken();
                mContainer.refreshData(fallList.getList());
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadResult(String.format(Locale.US, "已加载%d条", fallList.count()));
                }
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            if (getActivity() != null) {
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
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
                Toast.makeText(getActivity(), R.string.toast_timeout, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            if (getActivity() != null) {
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
                Toast.makeText(getActivity(), R.string.toast_cancel, Toast.LENGTH_SHORT).show();
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
            switch (v.getId()) {
            case R.id.btn_searchbar_close:
                mSearchBar.hide();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                break;
            }
        }
    };

    private RefreshGridView.OnUpdateListener onUpdateListener = new RefreshGridView.OnUpdateListener() {

        @Override
        public void onRefresh() {
            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    //TODO
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    mContainer.onRefreshComplete();
                }
            }.execute();
        }

        @Override
        public void onAppend() {
            if (mCallbackListener != null) {
                mCallbackListener.doLoadMore();
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
}
