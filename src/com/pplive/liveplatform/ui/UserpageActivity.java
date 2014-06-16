package com.pplive.liveplatform.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.alarm.AlarmCenter;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.GetProgramTask;
import com.pplive.liveplatform.core.task.user.RemoveProgramTask;
import com.pplive.liveplatform.core.task.user.UploadIconTask;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter.OnItemRightClickListener;
import com.pplive.liveplatform.ui.widget.dialog.IconDialog;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;
import com.pplive.liveplatform.ui.widget.image.CircularImageView;
import com.pplive.liveplatform.ui.widget.refresh.RefreshListView;
import com.pplive.liveplatform.util.DirManager;
import com.pplive.liveplatform.util.ImageUtil;

public class UserpageActivity extends Activity {

    static final String TAG = UserpageActivity.class.getSimpleName();

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

    private final static int REQUEST_CAMERA = 7803;

    private Context mContext;

    private List<Program> mPrograms;

    private String mUsername;

    private TextView mNicknameText;

    private RefreshListView mListView;

    private TextView mNodataText;

    private Button mNoDataButton;

    private CircularImageView mUserIcon;

    private UserpageProgramAdapter mAdapter;

    private RefreshDialog mRefreshDialog;

    private IconDialog mIconDialog;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

    private boolean mNeedUpdate;

    private Handler mPullHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPullHandler = new PullHandler(this);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);

        mNeedUpdate = true;
        mPrograms = new ArrayList<Program>();
        mAdapter = new UserpageProgramAdapter(this, mPrograms);
        mAdapter.setRightClickListener(mOnItemRightClickListener);
        mRefreshDialog = new RefreshDialog(this);
        mIconDialog = new IconDialog(this, R.style.icon_dialog);

        findViewById(R.id.btn_userpage_back).setOnClickListener(onBackBtnClickListener);
        mNoDataButton = (Button) findViewById(R.id.btn_userpage_record);
        mNoDataButton.setOnClickListener(mOnNodataBtnClickListener);
        Button settingsButton = (Button) findViewById(R.id.btn_userpage_settings);
        settingsButton.setOnClickListener(mOnSettingsBtnClickListener);

        mListView = (RefreshListView) findViewById(R.id.list_userpage_program);
        LinearLayout pullHeader = (LinearLayout) findViewById(R.id.layout_userpage_pull_header);
        pullHeader.addView(mListView.getPullView(), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setOnUpdateListener(onUpdateListener);

        mNicknameText = (TextView) findViewById(R.id.text_userpage_nickname);
        mUserIcon = (CircularImageView) findViewById(R.id.image_icon);
        mUserIcon.setOnClickListener(mOnIconClickListener);
        mNodataText = (TextView) findViewById(R.id.text_userpage_nodata);

        //init views
        TextView title = (TextView) findViewById(R.id.text_userpage_title);
        View cameraIcon = findViewById(R.id.image_userpage_camera);

        mUsername = getIntent().getStringExtra(EXTRA_USER);
        if (isLogin(mUsername)) {
            title.setText(R.string.userpage_my_title);
            settingsButton.setVisibility(View.VISIBLE);
            cameraIcon.setVisibility(View.VISIBLE);
            mListView.setSlidable(true);
        } else {
            title.setText(R.string.userpage_others_title);
            settingsButton.setVisibility(View.GONE);
            cameraIcon.setVisibility(View.GONE);
            mListView.setSlidable(false);
        }

        initUserinfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mNeedUpdate) {
            refreshData(false);
        }
    }

    private void initUserinfo() {
        mNicknameText.setText(getIntent().getStringExtra(EXTRA_NICKNAME));
        String iconUrl = getIntent().getStringExtra(EXTRA_ICON);
        if (!TextUtils.isEmpty(iconUrl)) {
            mUserIcon.setImageAsync(iconUrl, R.drawable.user_icon_default);
        } else {
            mUserIcon.setLocalImage(R.drawable.user_icon_default, true);
        }
    }

    private void refreshData(boolean isPull) {
        GetProgramTask task = new GetProgramTask();
        task.addTaskListener(onGetTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(GetProgramTask.KEY_USERNAME, mUsername);
        if (isLogin(mUsername)) {
            taskContext.set(GetProgramTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
        }
        if (isPull) {
            taskContext.set(GetProgramTask.KEY_TYPE, PULL);
        } else {
            mRefreshDialog.show();
            taskContext.set(GetProgramTask.KEY_TYPE, REFRESH);
        }
        task.execute(taskContext);
    }

    @Override
    protected void onDestroy() {
        mPullHandler.removeCallbacksAndMessages(null);
        mUserIcon.release();
        //        mListView.release();
        super.onDestroy();
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener mOnSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS);
        }
    };

    private View.OnClickListener mOnNodataBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goRecorderActivity(null);
        }
    };

    private View.OnClickListener mOnCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, R.string.toast_userpage_nopic, Toast.LENGTH_SHORT).show();
            } finally {
                mIconDialog.dismiss();
            }
        }
    };

    private View.OnClickListener mOnGalleryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICKPIC);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, R.string.toast_userpage_nopic, Toast.LENGTH_SHORT).show();
            } finally {
                mIconDialog.dismiss();
            }
        }

    };

    private View.OnClickListener mOnIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(mContext).isLogin(mUsername)) {
                mIconDialog.show();
                mIconDialog.setOnCameraClickListener(mOnCameraClickListener);
                mIconDialog.setOnGalleryClickListener(mOnGalleryClickListener);
            }
        }
    };

    private OnItemRightClickListener mOnItemRightClickListener = new OnItemRightClickListener() {

        @Override
        public void onRightClick(View v, final int position) {
            if (isLogin(mUsername)) {
                String title = mPrograms.get(position).getTitle();
                Dialog dialog = DialogManager.alertDeleteDialog(mContext, title, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long pid = mPrograms.get(position).getId();

                        RemoveProgramTask task = new RemoveProgramTask();
                        task.addTaskListener(onRemoveTaskListener);

                        TaskContext taskContext = new TaskContext();
                        taskContext.set(RemoveProgramTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
                        taskContext.set(RemoveProgramTask.KEY_PID, pid);

                        task.execute(taskContext);

                        mPrograms.remove(position);
                        mAdapter.notifyDataSetChanged();

                        if (mPrograms.isEmpty()) {
                            mNodataText.setText(R.string.userpage_user_nodata);
                            mNoDataButton.setEnabled(true);
                            findViewById(R.id.layout_userpage_nodata).setVisibility(View.VISIBLE);
                        }

                        AlarmCenter.getInstance(mContext).deletePrelive(pid);
                    }
                });
                dialog.show();
            }
            mListView.hideRight();
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick");
            Program program = mPrograms.get(position);
            if (program != null && mListView.canClick()) {
                switch (program.getLiveStatus()) {
                case LIVING:
                case STOPPED:
                    goPlayerActivity(program);
                    break;
                case NOT_START:
                case PREVIEW:
                case INIT:
                    if (isLogin(mUsername)) {
                        goRecorderActivity(program);
                    } else {
                        goPlayerActivity(program);
                    }
                    break;
                default:
                    break;
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

    private void uploadIcon(String imagePath) {
        UploadIconTask task = new UploadIconTask();
        task.addTaskListener(onIconTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(UploadIconTask.KEY_USERNAME, UserManager.getInstance(mContext).getUsernamePlain());
        taskContext.set(UploadIconTask.KEY_ICON_PATH, imagePath);
        taskContext.set(UploadIconTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
        task.execute(taskContext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == SettingsActivity.RESULT_LOGOUT) {
                finish();
            } else if (resultCode == SettingsActivity.RESULT_NICK_CHANGED) {
                mNicknameText.setText(UserManager.getInstance(mContext).getNickname());
            }
        } else if (requestCode == REQUEST_PICKPIC && resultCode == Activity.RESULT_OK) {
            if (!UserManager.getInstance(mContext).isLogin(mUsername)) {
                return;
            }
            try {
                mRefreshDialog.show();
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                uploadIcon(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                Toast.makeText(mContext, R.string.toast_icon_processing, Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Column does not exist");
                Toast.makeText(mContext, R.string.toast_icon_image_error, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = ImageUtil.getScaledBitmapLimit((Bitmap) data.getExtras().get("data"), 160);
            String filename = DirManager.getImageCachePath() + "/upload_icon.tmp";
            if (ImageUtil.bitmap2File(bitmap, filename)) {
                uploadIcon(filename);
                Toast.makeText(mContext, R.string.toast_icon_processing, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.toast_icon_image_error, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Task.TaskListener onIconTaskListener = new Task.BaseTaskListener() {

        @Override
        public void onTaskSucceed(Object sender, TaskSucceedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_icon_changed, Toast.LENGTH_SHORT).show();
            UserManager.getInstance(mContext).setUserinfo((User) event.getContext().get(UploadIconTask.KEY_USERINFO));
            String iconUrl = UserManager.getInstance(mContext).getIcon();
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserIcon.setImageAsync(iconUrl, R.drawable.user_icon_default);
            } else {
                mUserIcon.setLocalImage(R.drawable.user_icon_default, true);
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

    private Task.TaskListener onRemoveTaskListener = new Task.BaseTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Toast.makeText(mContext, R.string.toast_userpage_delete_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskSucceed(Object sender, TaskSucceedEvent event) {
            Toast.makeText(mContext, R.string.toast_userpage_delete_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Toast.makeText(mContext, R.string.toast_userpage_delete_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
        }

    };

    private Task.TaskListener onGetTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Object sender, TaskSucceedEvent event) {
            mNeedUpdate = false;
            mRefreshDialog.dismiss();
            mListView.setLastUpdateTime(System.currentTimeMillis());
            if ((Integer) event.getContext().get(GetProgramTask.KEY_TYPE) == PULL) {
                mPullHandler.sendEmptyMessage(MSG_PULL_FINISH);
            }
            mPrograms.clear();
            mPrograms.addAll((Collection<Program>) event.getContext().get(GetProgramTask.KEY_RESULT));
            Collections.sort(mPrograms, comparator);
            mAdapter.notifyDataSetChanged();
            if (mPrograms.isEmpty()) {
                if (isLogin(mUsername)) {
                    mNodataText.setText(R.string.userpage_user_nodata);
                    mNoDataButton.setEnabled(true);
                } else {
                    mNodataText.setText(R.string.userpage_others_nodata);
                    mNoDataButton.setEnabled(false);
                }
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
            mNoDataButton.setEnabled(true);
            mNoDataButton.setOnClickListener(onErrorBtnClickListener);
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

    private RefreshListView.OnUpdateListener onUpdateListener = new RefreshListView.OnUpdateListener() {
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

    private boolean isLogin(String username) {
        return UserManager.getInstance(this).isLogin(username);
    }

    private void goPlayerActivity(Program program) {
        Intent intent = new Intent(mContext, LivePlayerActivity.class);
        intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
        startActivity(intent);
    }

    private void goRecorderActivity(Program program) {
        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            mNeedUpdate = true;
            Intent intent = new Intent(mContext, LiveRecordActivity.class);
            if (program != null) {
                intent.putExtra(LiveRecordActivity.EXTRA_PROGRAM, program);
            }
            startActivity(intent);
        } else {
            Toast.makeText(mContext, R.string.toast_version_low, Toast.LENGTH_LONG).show();
        }
    }

    static class PullHandler extends Handler {
        private WeakReference<UserpageActivity> mOuter;

        public PullHandler(UserpageActivity outer) {
            mOuter = new WeakReference<UserpageActivity>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            UserpageActivity outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                case MSG_PULL_DELAY:
                    outer.mRefreshDelayed = true;
                    break;
                case MSG_PULL_FINISH:
                    outer.mRefreshFinish = true;
                    break;
                case MSG_PULL_TIMEOUT:
                    outer.mListView.onRefreshComplete();
                    return;
                }
                if (outer.mRefreshDelayed && outer.mRefreshFinish) {
                    removeMessages(MSG_PULL_TIMEOUT);
                    outer.mListView.onRefreshComplete();
                }
            }
        }
    }

}
