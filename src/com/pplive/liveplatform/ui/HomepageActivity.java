package com.pplive.liveplatform.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ScrollView;
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
import com.pplive.liveplatform.core.task.homepage.GetTask;
import com.pplive.liveplatform.ui.homepage.program.ProgramView;

public class HomepageActivity extends FragmentActivity {
    static final String TAG = "HomepageActivity";

    private Dialog mRefreshDialog;

    private ScrollView mContainerScrollView;

    private Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.layout_homepage_fragment);

        mContainerScrollView = (ScrollView) findViewById(R.id.scrollview_homepage);

        mRefreshDialog = new Dialog(mContext, R.style.homepage_refresh_dialog);
        mRefreshDialog.setContentView(R.layout.dialog_refresh);

        startTask();
    }

    private void startTask() {
        GetTask task = new GetTask();
        task.addTaskFinishedListener(getTaskFinishedListener);
        task.addTaskTimeoutListener(getTaskTimeoutListener);
        task.addTaskCancelListener(getOnTaskCancelListner);
        task.addTaskFailedListener(getTaskFailedListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(GetTask.KEY_URL, "http://172.16.10.128:8080/search/v1/pptv/search");
        mRefreshDialog.show();
        task.execute(taskContext);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private OnTaskFinishedListener getTaskFinishedListener = new OnTaskFinishedListener() {
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_sucess, Toast.LENGTH_SHORT).show();
            String response = (String) event.getContext().get(GetTask.KEY_RESULT);
            Log.d(TAG, response);
            JsonObject jsonElement = new JsonParser().parse(response).getAsJsonObject();
            ProgramView pv = new ProgramView();
            pv.setData(jsonElement);
            mContainerScrollView.removeAllViews();
            mContainerScrollView.addView(pv.getView(mContext));
        }
    };

    private OnTaskTimeoutListener getTaskTimeoutListener = new OnTaskTimeoutListener() {
        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }
    };

    private OnTaskFailedListener getTaskFailedListener = new OnTaskFailedListener() {
        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        }
    };

    private OnTaskCancelListner getOnTaskCancelListner = new OnTaskCancelListner() {
        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_cancel, Toast.LENGTH_SHORT).show();
        }
    };
}
