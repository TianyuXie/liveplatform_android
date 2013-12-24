package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class UserpageActivity extends Activity {
    static final String TAG = "_UserpageActivity";

    private static final DisplayImageOptions DEFAULT_ICON_DISPLAY_OPTIONS = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.user_icon_default).showImageForEmptyUri(R.drawable.user_icon_default).showStubImage(R.drawable.user_icon_default)
            .cacheInMemory(true).build();

    private List<Program> mPrograms;
    private CircularImageView mUserIcon;
    private TextView mNicknameText;
    private UserpageProgramAdapter mAdapter;
    private RefreshDialog refreshDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);

        mPrograms = new ArrayList<Program>();
        mAdapter = new UserpageProgramAdapter(this, mPrograms);

        findViewById(R.id.btn_userpage_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_userpage_settings).setOnClickListener(onSettingsBtnClickListener);
        findViewById(R.id.btn_userpage_record).setOnClickListener(onRecordBtnClickListener);

        ListView listView = (ListView) findViewById(R.id.list_userpage_program);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        mUserIcon = (CircularImageView) findViewById(R.id.btn_userpage_user_icon);
        mNicknameText = (TextView) findViewById(R.id.text_userpage_nickname);
        refreshDialog = new RefreshDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        refreshData();
    }

    private void refreshData() {
        refreshDialog.show();
        ProgramTask task = new ProgramTask();
        task.addTaskListener(onTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(ProgramTask.KEY_USERNAME, UserManager.getInstance(this).getUsernamePlain());
        task.execute(taskContext);
        updateUsername();
    }

    @Override
    protected void onDestroy() {
        mUserIcon.release();
        super.onDestroy();
    }

    private void updateUsername() {
        if (UserManager.getInstance(this).isLogin()) {
            mNicknameText.setText(UserManager.getInstance(this).getNickname());
            String iconUrl = UserManager.getInstance(this).getIcon();
            mUserIcon.setRounded(false);
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserIcon.setImageAsync(iconUrl, DEFAULT_ICON_DISPLAY_OPTIONS, imageLoadingListener);
            } else {
                mUserIcon.setImageResource(R.drawable.user_icon_default);
            }
        } else {
            mNicknameText.setText("");
            mUserIcon.setImageResource(R.drawable.user_icon_login);
            mUserIcon.setRounded(false);
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Program program = mPrograms.get(position);
            if (program != null) {
                Intent intent = new Intent();
                switch (program.getLiveStatus()) {
                case LIVING:
                case STOPPED:
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    intent.setClass(UserpageActivity.this, LivePlayerActivity.class);
                    startActivity(intent);
                    break;
                case NOT_START:
                case PREVIEW:
                case INIT:
                    intent.setClass(UserpageActivity.this, LiveRecordActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
                }
            }
        }
    };

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
            startActivityForResult(intent, SettingsActivity.FROM_USERPAGE);
        }
    };

    private View.OnClickListener onRecordBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UserpageActivity.this, LiveRecordActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.FROM_USERPAGE && resultCode == SettingsActivity.LOGOUT) {
            finish();
        }
    };

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            refreshDialog.dismiss();
            mPrograms.clear();
            mPrograms.addAll((List<Program>) event.getContext().get(ProgramTask.KEY_RESULT));
            Collections.sort(mPrograms, comparator);
            mAdapter.notifyDataSetChanged();
            if (mPrograms.isEmpty()) {
                findViewById(R.id.layout_userpage_nodata).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.layout_userpage_nodata).setVisibility(View.GONE);
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            refreshDialog.dismiss();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            refreshDialog.dismiss();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            refreshDialog.dismiss();
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
            mUserIcon.setRounded(false);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.d(TAG, "onLoadingComplete");
            mUserIcon.setRounded(arg2 != null);
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            Log.d(TAG, "onLoadingCancelled");
            mUserIcon.setRounded(false);
        }
    };

    private Comparator<Program> comparator = new Comparator<Program>() {

        @Override
        public int compare(Program lhs, Program rhs) {

            if (lhs.getLiveStatus().ordinal() < rhs.getLiveStatus().ordinal()) {
                return -1;
            } else if (lhs.getLiveStatus().ordinal() > rhs.getLiveStatus().ordinal()) {
                return 1;
            } else {
                return 0;
            }
        }
    };
}
