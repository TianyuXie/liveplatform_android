package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.ProgramTask;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter;
import com.pplive.liveplatform.ui.widget.CircularImageView;

public class UserpageActivity extends Activity {
    static final String TAG = "_UserpageActivity";

    private static final DisplayImageOptions DEFAULT_ICON_DISPLAY_OPTIONS = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.user_icon_default).showImageForEmptyUri(R.drawable.user_icon_default).showStubImage(R.drawable.user_icon_default)
            .build();

    private List<Program> mPrograms;
    private ListView mListView;
    private CircularImageView mUserButton;
    private TextView mUserTextView;
    private UserpageProgramAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);

        mPrograms = new ArrayList<Program>();
        mAdapter = new UserpageProgramAdapter(this, mPrograms);

        findViewById(R.id.btn_userpage_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_userpage_settings).setOnClickListener(onSettingsBtnClickListener);
        mListView = (ListView) findViewById(R.id.list_userpage_program);
        mListView.setAdapter(mAdapter);
        mUserButton = (CircularImageView) findViewById(R.id.btn_userpage_user_icon);
        mUserTextView = (TextView) findViewById(R.id.text_userpage_nickname);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgramTask task = new ProgramTask();
        task.addTaskListener(onTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(ProgramTask.KEY_USR, UserManager.getInstance(this).getActiveUserPlain());
        task.execute(taskContext);
        updateUsername();
    }

    private void updateUsername() {
        if (UserManager.getInstance(this).isLogin()) {
            mUserTextView.setText(UserManager.getInstance(this).getNickname());
            String iconUrl = UserManager.getInstance(this).getIcon();
            mUserButton.setRounded(false);
            Log.d(TAG, iconUrl);
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserButton.setImageAsync(UserManager.getInstance(this).getIcon(), DEFAULT_ICON_DISPLAY_OPTIONS, imageLoadingListener);
            } else {
                mUserButton.setImageResource(R.drawable.user_icon_default);
            }
        } else {
            mUserTextView.setText("");
            mUserButton.setImageResource(R.drawable.user_icon_login);
            mUserButton.setRounded(false);
        }
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener onSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UserpageActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    };

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mPrograms.clear();
            mPrograms.addAll((List<Program>) event.getContext().get(ProgramTask.KEY_RESULT));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            // TODO Auto-generated method stub

        }
    };

    private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
            Log.d(TAG, "onLoadingStarted");
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            Log.d(TAG, "onLoadingFailed");
            mUserButton.setRounded(false);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.d(TAG, "onLoadingComplete");
            mUserButton.setRounded(arg2 != null);
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            Log.d(TAG, "onLoadingCancelled");
            mUserButton.setRounded(false);
        }
    };

}
