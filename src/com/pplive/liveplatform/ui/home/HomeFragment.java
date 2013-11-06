package com.pplive.liveplatform.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

public class HomeFragment extends Fragment {
    static final String TAG = "HomepageFragment";

    private Dialog mRefreshDialog;

    private LinearLayout mContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.layout_home_fragment, container, false);
        mContainer = (LinearLayout) layout.findViewById(R.id.layout_home_body);
        mRefreshDialog = new Dialog(getActivity(), R.style.homepage_refresh_dialog);
        mRefreshDialog.setContentView(R.layout.dialog_refresh);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        startTask();
    }

    private void startTask() {
        GetTask task = new GetTask();
        task.addTaskFinishedListener(getTaskFinishedListener);
        task.addTaskTimeoutListener(getTaskTimeoutListener);
        task.addTaskCancelListener(getOnTaskCancelListner);
        task.addTaskFailedListener(getTaskFailedListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(GetTask.KEY_URL, "http://42.96.137.0:33677/");
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
}
