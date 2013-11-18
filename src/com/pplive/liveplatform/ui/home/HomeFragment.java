package com.pplive.liveplatform.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.task.Task.OnTaskCancelListner;
import com.pplive.liveplatform.core.task.Task.OnTaskFailedListener;
import com.pplive.liveplatform.core.task.Task.OnTaskFinishedListener;
import com.pplive.liveplatform.core.task.Task.OnTaskTimeoutListener;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.home.GetTask;
import com.pplive.liveplatform.ui.home.program.RefreshContainer;
import com.pplive.liveplatform.ui.widget.SearchBar;
import com.pplive.liveplatform.ui.widget.TitleBar;
import com.pplive.liveplatform.ui.widget.intercept.InterceptDetector;
import com.pplive.liveplatform.ui.widget.intercept.InterceptableRelativeLayout;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;
import com.pplive.liveplatform.util.ConfigUtil;
import com.pplive.liveplatform.util.KeyUtil;

public class HomeFragment extends Fragment implements SlidableContainer.OnSlideListener {
    static final String TAG = "HomeFragment";

    private TitleBar mTitleBar;

    private RefreshContainer mContainer;

    private SearchBar mSearchBar;

    private Callback mCallbackListener;

    private boolean mSlided;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        InterceptableRelativeLayout layout = (InterceptableRelativeLayout) inflater.inflate(R.layout.layout_home_fragment, container, false);
        mContainer = (RefreshContainer) layout.findViewById(R.id.layout_home_body);
//        mContainer.setOnScrollRefreshListener(scrollRefreshListener);
        mTitleBar = (TitleBar) layout.findViewById(R.id.titlebar_home);
        mTitleBar.setOnClickListener(titleBarOnClickListener);
        mSearchBar = (SearchBar) layout.findViewById(R.id.searchbar_home);
        mSearchBar.setOnClickListener(searchBarOnClickListener);
        layout.setGestureDetector(new InterceptDetector(getActivity(), onGestureListener));
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        startTask();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void startTask() {
        GetTask task = new GetTask();
        task.addTaskFinishedListener(getTaskFinishedListener);
        task.addTaskTimeoutListener(getTaskTimeoutListener);
        task.addTaskCancelListener(getOnTaskCancelListner);
        task.addTaskFailedListener(getTaskFailedListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(GetTask.KEY_URL, ConfigUtil.getString(KeyUtil.HTTP_HOME_TEST_URL));
        task.execute(taskContext);
        if (mCallbackListener != null) {
            mCallbackListener.doLoadMore();
        }
    }

    private OnTaskFinishedListener getTaskFinishedListener = new OnTaskFinishedListener() {
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.toast_sucess, Toast.LENGTH_SHORT).show();
                String response = (String) event.getContext().get(GetTask.KEY_RESULT);
                Log.d(TAG, response);
                JsonObject jsonElement = new JsonParser().parse(response).getAsJsonObject();
                mContainer.refreshData(jsonElement);
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadResult("已加载10条");
                }
            }
        }
    };

    private OnTaskTimeoutListener getTaskTimeoutListener = new OnTaskTimeoutListener() {
        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            if (getActivity() != null) {
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadResult("已加载10条");
                }
                Toast.makeText(getActivity(), R.string.toast_timeout, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnTaskFailedListener getTaskFailedListener = new OnTaskFailedListener() {
        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            if (getActivity() != null) {
                if (mCallbackListener != null) {
                    mCallbackListener.doLoadFinish();
                }
                Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnTaskCancelListner getOnTaskCancelListner = new OnTaskCancelListner() {
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
    }

    @Override
    public void onSlideBack() {
        mSlided = false;
        mTitleBar.setMenuButtonHighlight(false);
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
                break;
            }
        }
    };

//    private RefreshContainer.OnScrollRefreshListener scrollRefreshListener = new RefreshContainer.OnScrollRefreshListener() {
//
//        @Override
//        public void onReachTop() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReachBottom() {
//            if (mCallbackListener != null) {
//                mCallbackListener.doLoadMore();
//            }
//        }
//    };

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
