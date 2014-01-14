package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.ProgramTask;
import com.pplive.liveplatform.core.task.user.UploadIconTask;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;
import com.pplive.liveplatform.ui.widget.image.CircularImageView;
import com.pplive.liveplatform.ui.widget.refresh.RefreshSwipeListView;
import com.pplive.liveplatform.ui.widget.swipe.BaseSwipeListViewListener;
import com.pplive.liveplatform.ui.widget.swipe.SwipeListViewListener;

public class UserpageActivity extends Activity {
    static final String TAG = "_UserpageActivity";

    public static final String EXTRA_USER = "username";

    public static final String EXTRA_ICON = "icon";

    public static final String EXTRA_NICKNAME = "nickname";

    private final static int PULL = 1002;

    private final static int REFRESH = 1003;

    private final static int MSG_PULL_DELAY = 2000;

    private final static int MSG_PULL_FINISH = 2001;

    private final static int MSG_PULL_TIMEOUT = 2002;

    private final static int PULL_DELAY_TIME = 2000;

    private final static int PULL_TIMEOUT_TIME = 10000;

    private final static int REQUEST_PICKPIC = 7801;

    private final static int REQUEST_SETTINGS = 7802;

    private final static int REQUEST_RECORD = 7803;

    private Context mContext;
    private List<Program> mPrograms;
    private String mUsername;

    private TextView mNicknameText;
    private RefreshSwipeListView mListView;
    private TextView mNodataText;
    private Button mNodataButton;
    private CircularImageView mUserIcon;
    private UserpageProgramAdapter mAdapter;
    private RefreshDialog mRefreshDialog;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);

        mPrograms = new ArrayList<Program>();
        mAdapter = new UserpageProgramAdapter(this, mPrograms);
        mRefreshDialog = new RefreshDialog(this);

        findViewById(R.id.btn_userpage_back).setOnClickListener(onBackBtnClickListener);
        mNodataButton = (Button) findViewById(R.id.btn_userpage_record);
        mNodataButton.setOnClickListener(onNodataBtnClickListener);
        Button settingsButton = (Button) findViewById(R.id.btn_userpage_settings);
        settingsButton.setOnClickListener(onSettingsBtnClickListener);

        mListView = (RefreshSwipeListView) findViewById(R.id.list_userpage_program);
        LinearLayout pullHeader = (LinearLayout) findViewById(R.id.layout_userpage_pull_header);
        pullHeader.addView(mListView.getPullView(), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mListView.setAdapter(mAdapter);
        mListView.setOnUpdateListener(onUpdateListener);
        mListView.setSwipeListViewListener(swipeListViewListener);

        mNicknameText = (TextView) findViewById(R.id.text_userpage_nickname);
        mUserIcon = (CircularImageView) findViewById(R.id.image_userpage_icon);
        mUserIcon.setOnClickListener(onIconClickListener);
        mNodataText = (TextView) findViewById(R.id.text_userpage_nodata);

        //init views
        TextView title = (TextView) findViewById(R.id.text_userpage_title);
        View cameraIcon = findViewById(R.id.image_userpage_camera);
        mUsername = getIntent().getStringExtra(EXTRA_USER);
        if (isLogin(mUsername)) {
            title.setText(R.string.userpage_my_title);
            settingsButton.setVisibility(View.VISIBLE);
            cameraIcon.setVisibility(View.VISIBLE);
        } else {
            title.setText(R.string.userpage_others_title);
            settingsButton.setVisibility(View.GONE);
            cameraIcon.setVisibility(View.GONE);
        }
        initUserinfo();
        refreshData(false);
    }

    private void initUserinfo() {
        mNicknameText.setText(getIntent().getStringExtra(EXTRA_NICKNAME));
        String iconUrl = getIntent().getStringExtra(EXTRA_ICON);
        if (!TextUtils.isEmpty(iconUrl)) {
            mUserIcon.setImageAsync(iconUrl, R.drawable.user_icon_default, imageLoadingListener);
        } else {
            mUserIcon.setLocalImage(R.drawable.user_icon_default);
        }
    }

    private void refreshData(boolean isPull) {
        ProgramTask task = new ProgramTask();
        task.addTaskListener(onProgramTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(ProgramTask.KEY_USERNAME, mUsername);
        if (isLogin(mUsername)) {
            taskContext.set(ProgramTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
        }
        if (isPull) {
            taskContext.set(ProgramTask.KEY_TYPE, PULL);
        } else {
            mRefreshDialog.show();
            taskContext.set(ProgramTask.KEY_TYPE, REFRESH);
        }
        task.execute(taskContext);
    }

    @Override
    protected void onDestroy() {
        mUserIcon.release();
        super.onDestroy();
    }

    private SwipeListViewListener swipeListViewListener = new BaseSwipeListViewListener() {
        @Override
        public void onStartOpen(int position, int action, boolean right) {
            Log.d(TAG, "onStartOpen");
        }

        @Override
        public void onStartClose(int position, boolean right) {
            Log.d(TAG, "onStartClose");
        }

        @Override
        public void onClickFrontView(int position) {
            Log.d(TAG, "onClickFrontView");
            Program program = mPrograms.get(position);
            if (program != null && mListView.canClick()) {
                Intent intent = new Intent();
                switch (program.getLiveStatus()) {
                case LIVING:
                case STOPPED:
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    intent.setClass(mContext, LivePlayerActivity.class);
                    startActivity(intent);
                    break;
                case NOT_START:
                case PREVIEW:
                case INIT:
                    if (isLogin(mUsername)) {
                        intent.putExtra(LiveRecordActivity.EXTRA_PROGRAM, program);
                        intent.setClass(mContext, LiveRecordActivity.class);
                        startActivityForResult(intent, REQUEST_RECORD);
                    } else {
                        intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                        intent.setClass(mContext, LivePlayerActivity.class);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
                }
            }
        }

        @Override
        public void onClickBackView(int position) {
            Log.d(TAG, "onClickBackView");
        }

        @Override
        public void onDismiss(int[] reverseSortedPositions) {
            Log.d(TAG, "onDismiss");
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
            Intent intent = new Intent(mContext, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS);
        }
    };

    private View.OnClickListener onNodataBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, LiveRecordActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(mContext).isLogin(mUsername)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_PICKPIC);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, R.string.toast_userpage_nopic, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private View.OnClickListener onErrorBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshData(false);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == SettingsActivity.RESULT_LOGOUT) {
                finish();
            } else if (resultCode == SettingsActivity.RESULT_NICK_CHANGED) {
                mNicknameText.setText(UserManager.getInstance(mContext).getNickname());
            }
        } else if (requestCode == REQUEST_RECORD) {
            refreshData(false);
        } else if (requestCode == REQUEST_PICKPIC && resultCode == Activity.RESULT_OK) {
            if (!UserManager.getInstance(mContext).isLogin(mUsername)) {
                return;
            }
            try {
                mRefreshDialog.show();
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                UploadIconTask task = new UploadIconTask();
                task.addTaskListener(onIconTaskListener);
                TaskContext taskContext = new TaskContext();
                taskContext.set(UploadIconTask.KEY_USERNAME, UserManager.getInstance(mContext).getUsernamePlain());
                taskContext.set(UploadIconTask.KEY_ICON_PATH, imagePath);
                taskContext.set(UploadIconTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
                task.execute(taskContext);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Column does not exist");
            }
        }
    };

    private Task.OnTaskListener onIconTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_icon_changed, Toast.LENGTH_SHORT).show();
            UserManager.getInstance(mContext).setUserinfo((User) event.getContext().get(UploadIconTask.KEY_USERINFO));
            String iconUrl = UserManager.getInstance(mContext).getIcon();
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserIcon.setImageAsync(iconUrl, R.drawable.user_icon_default, imageLoadingListener);
            } else {
                mUserIcon.setLocalImage(R.drawable.user_icon_default);
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_icon_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
        }
    };

    private Task.OnTaskListener onProgramTaskListener = new Task.OnTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mRefreshDialog.dismiss();
            mListView.setLastUpdateTime(System.currentTimeMillis());
            if ((Integer) event.getContext().get(ProgramTask.KEY_TYPE) == PULL) {
                mPullHandler.sendEmptyMessage(MSG_PULL_FINISH);
            }
            mPrograms.clear();
            mPrograms.addAll((Collection<Program>) event.getContext().get(ProgramTask.KEY_RESULT));
            Collections.sort(mPrograms, comparator);
            mAdapter.notifyDataSetChanged();
            if (mPrograms.isEmpty()) {
                if (isLogin(mUsername)) {
                    mNodataText.setText(R.string.userpage_user_nodata);
                    mNodataButton.setEnabled(true);
                } else {
                    mNodataText.setText(R.string.userpage_others_nodata);
                    mNodataButton.setEnabled(false);
                }
                mNodataButton.setOnClickListener(onNodataBtnClickListener);
                findViewById(R.id.layout_userpage_nodata).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.layout_userpage_nodata).setVisibility(View.GONE);
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "onTaskFailed");
            mRefreshDialog.dismiss();
            mPullHandler.sendEmptyMessage(MSG_PULL_FINISH);
            mPrograms.clear();
            mAdapter.notifyDataSetChanged();
            mNodataText.setText(R.string.userpage_user_error);
            mNodataButton.setEnabled(true);
            mNodataButton.setOnClickListener(onErrorBtnClickListener);
            findViewById(R.id.layout_userpage_nodata).setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            onTaskFailed(sender, null);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
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
                if (lhs.isPrelive()) {
                    if (lhs.getStartTime() > rhs.getStartTime()) {
                        return 1;
                    } else if (lhs.getStartTime() < rhs.getStartTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } else {
                    if (lhs.getStartTime() > rhs.getStartTime()) {
                        return -1;
                    } else if (lhs.getStartTime() < rhs.getStartTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    };

    private RefreshSwipeListView.OnUpdateListener onUpdateListener = new RefreshSwipeListView.OnUpdateListener() {
        @Override
        public void onRefresh() {
            mRefreshFinish = false;
            mRefreshDelayed = false;
            refreshData(true);
            mPullHandler.sendEmptyMessageDelayed(MSG_PULL_DELAY, PULL_DELAY_TIME);
            mPullHandler.sendEmptyMessageDelayed(MSG_PULL_TIMEOUT, PULL_TIMEOUT_TIME);
        }

        @Override
        public void onAppend() {
        }

        @Override
        public void onScrollDown(boolean isDown) {
        }
    };

    private Handler mPullHandler = new Handler() {
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
                mListView.onRefreshComplete();
                return;
            }
            if (mRefreshDelayed && mRefreshFinish) {
                mPullHandler.removeMessages(MSG_PULL_TIMEOUT);
                mListView.onRefreshComplete();
            }
        }
    };

    private boolean isLogin(String username) {
        return UserManager.getInstance(this).isLogin(username);
    }

}
