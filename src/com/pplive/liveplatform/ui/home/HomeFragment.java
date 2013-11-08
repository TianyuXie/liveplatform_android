package com.pplive.liveplatform.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.pplive.liveplatform.ui.home.program.ProgramView;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.TitleBar;
import com.pplive.liveplatform.ui.widget.intercept.InterceptDetector;
import com.pplive.liveplatform.ui.widget.intercept.InterceptableRelativeLayout;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;
import com.pplive.liveplatform.util.ConfigUtil;
import com.pplive.liveplatform.util.KeyUtil;

public class HomeFragment extends Fragment implements SlidableContainer.OnSlideListener {
    static final String TAG = "HomepageFragment";

    private Dialog mRefreshDialog;

    private LinearLayout mContainer;

    private TitleBar mTitleBar;

    private Callback mCallbackListener;

    private boolean mSlided;

    private LoadingButton mStatusButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        InterceptableRelativeLayout layout = (InterceptableRelativeLayout) inflater.inflate(R.layout.layout_home_fragment, container, false);
        mContainer = (LinearLayout) layout.findViewById(R.id.layout_home_body);
        mTitleBar = (TitleBar) layout.findViewById(R.id.titlebar_home);
        mStatusButton = (LoadingButton) layout.findViewById(R.id.btn_home_status);
        mStatusButton.setBackgroundResource(R.drawable.home_status_btn_bg, R.drawable.home_status_btn_loading);
        mStatusButton.setAnimation(R.anim.home_status_rotate);
        mStatusButton.setAnimationResource(R.drawable.home_status_btn_anim);
        mTitleBar.setCallbackListener(titleBarCallbackListner);
        mRefreshDialog = new Dialog(getActivity(), R.style.homepage_refresh_dialog);
        mRefreshDialog.setContentView(R.layout.dialog_refresh);
        layout.setGestureDetector(new InterceptDetector(getActivity(), onGestureListener));
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        startTask();
        mStatusButton.startLoading("正在加载");
    }

    private void startTask() {
        GetTask task = new GetTask();
        task.addTaskFinishedListener(getTaskFinishedListener);
        task.addTaskTimeoutListener(getTaskTimeoutListener);
        task.addTaskCancelListener(getOnTaskCancelListner);
        task.addTaskFailedListener(getTaskFailedListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(GetTask.KEY_URL, ConfigUtil.getString(KeyUtil.HTTP_HOME_TEST_URL));
        mRefreshDialog.show();
        task.execute(taskContext);
    }

    private OnTaskFinishedListener getTaskFinishedListener = new OnTaskFinishedListener() {
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            if (getActivity() != null) {
                mRefreshDialog.dismiss();
                Toast.makeText(getActivity(), R.string.toast_sucess, Toast.LENGTH_SHORT).show();

                String response = (String) event.getContext().get(GetTask.KEY_RESULT);
                Log.d(TAG, response);
                JsonObject jsonElement = new JsonParser().parse(response).getAsJsonObject();

                ProgramView pv = new ProgramView(getActivity().getApplicationContext());
                pv.updateData(jsonElement);
                mContainer.removeAllViews();
                mContainer.addView(pv.getView());
                mStatusButton.finishLoading();
            }
        }
    };

    private OnTaskTimeoutListener getTaskTimeoutListener = new OnTaskTimeoutListener() {
        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            if (getActivity() != null) {
                mRefreshDialog.dismiss();
                Toast.makeText(getActivity(), R.string.toast_timeout, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnTaskFailedListener getTaskFailedListener = new OnTaskFailedListener() {
        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            if (getActivity() != null) {
                mRefreshDialog.dismiss();
                Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnTaskCancelListner getOnTaskCancelListner = new OnTaskCancelListner() {
        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            if (getActivity() != null) {
                mRefreshDialog.dismiss();
                Toast.makeText(getActivity(), R.string.toast_cancel, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onSlide() {
        mSlided = true;
        mTitleBar.setMenuButtonHighlight(true);
        mStatusButton.hide();
    }

    @Override
    public void onSlideBack() {
        mSlided = false;
        mTitleBar.setMenuButtonHighlight(false);
        mStatusButton.show();
    }

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

    public interface Callback {
        public void doSlide();

        public void doSlideBack();
    }

    public void setCallbackListener(Callback listener) {
        this.mCallbackListener = listener;
    }

    private TitleBar.Callback titleBarCallbackListner = new TitleBar.Callback() {
        @Override
        public void doSlide() {
            if (mCallbackListener != null) {
                mCallbackListener.doSlide();
            }
        }
    };

}
