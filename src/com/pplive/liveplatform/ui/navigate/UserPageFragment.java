package com.pplive.liveplatform.ui.navigate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.LiveRecordActivity;
import com.pplive.liveplatform.ui.SettingsActivity;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter.OnItemRightClickListener;
import com.pplive.liveplatform.ui.widget.dialog.IconDialog;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;
import com.pplive.liveplatform.ui.widget.image.RoundedImageView;
import com.pplive.liveplatform.ui.widget.refresh.RefreshListView;
import com.pplive.liveplatform.util.DirManager;
import com.pplive.liveplatform.util.ImageUtil;

public class UserPageFragment extends Fragment {

    static final String TAG = UserPageFragment.class.getSimpleName();

    public static final String EXTRA_USERNAME = "username";

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

    private Activity mActivity;

    private List<Program> mPrograms;

    private String mUsername;

    private String mNickName;

    private String mIconUrl;

    private TextView mTextNickName;

    private ImageButton mBtnSettings;

    private RoundedImageView mUserIcon;

    private RefreshListView mListView;

    private TextView mNodataText;

    private Button mNoDataButton;

    private UserpageProgramAdapter mAdapter;

    private RefreshDialog mRefreshDialog;

    private IconDialog mIconDialog;

    private boolean mRefreshFinish;

    private boolean mRefreshDelayed;

    private Handler mPullHandler;

    private View mNoDataView;

    private View mCameraIcon;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_userpage, container, false);

        mPullHandler = new PullHandler(this);

        mPrograms = new ArrayList<Program>();
        mAdapter = new UserpageProgramAdapter(mActivity, mPrograms);
        mAdapter.setRightClickListener(mOnItemRightClickListener);
        mRefreshDialog = new RefreshDialog(mActivity);
        mIconDialog = new IconDialog(mActivity, R.style.icon_dialog);

        mNoDataButton = (Button) layout.findViewById(R.id.btn_userpage_record);
        mNoDataButton.setOnClickListener(mOnNodataBtnClickListener);

        mBtnSettings = (ImageButton) layout.findViewById(R.id.btn_settings);
        mBtnSettings.setOnClickListener(mOnSettingsBtnClickListener);

        mListView = (RefreshListView) layout.findViewById(R.id.list_userpage_program);
        LinearLayout pullHeader = (LinearLayout) layout.findViewById(R.id.layout_userpage_pull_header);
        pullHeader.addView(mListView.getPullView(), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setOnUpdateListener(onUpdateListener);

        mTextNickName = (TextView) layout.findViewById(R.id.text_userpage_nickname);
        mUserIcon = (RoundedImageView) layout.findViewById(R.id.image_icon);
        mUserIcon.setOnClickListener(mOnIconClickListener);

        mNodataText = (TextView) layout.findViewById(R.id.text_userpage_nodata);
        mCameraIcon = layout.findViewById(R.id.image_userpage_camera);
        //init views

        mNoDataView = layout.findViewById(R.id.layout_userpage_nodata);

        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        mPrograms.clear();
        mAdapter.notifyDataSetChanged();

    }

    public void setIntent(Intent intent) {
        if (null != intent) {
            mUsername = intent.getStringExtra(EXTRA_USERNAME);
            mNickName = intent.getStringExtra(EXTRA_NICKNAME);
            mIconUrl = intent.getStringExtra(EXTRA_ICON);
        } else {
            mUsername = null;
            mNickName = null;
            mIconUrl = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResumre");

        // TODO: Reuse with UserpageActivity;
        mUsername = UserManager.getInstance(getActivity()).getUsernamePlain();
        mIconUrl = UserManager.getInstance(getActivity()).getIcon();
        mNickName = UserManager.getInstance(getActivity()).getNickname();

        if (isLogin(mUsername)) {
            mCameraIcon.setVisibility(View.VISIBLE);
            mListView.setSlidable(true);
        } else {
            mCameraIcon.setVisibility(View.GONE);
            mListView.setSlidable(false);
        }

        initUserinfo();

        refreshData(false);
    }

    private void initUserinfo() {
        mTextNickName.setText(mNickName);
        if (!TextUtils.isEmpty(mIconUrl)) {
            mUserIcon.setImageAsync(mIconUrl, R.drawable.user_icon_default);
        } else {
            //            mUserIcon.setLocalImage(R.drawable.user_icon_default, true);
        }
    }

    private void refreshData(boolean isPull) {
        if (TextUtils.isEmpty(mUsername)) {
            return;
        }
        GetProgramTask task = new GetProgramTask();
        task.addTaskListener(onGetTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(GetProgramTask.KEY_USERNAME, mUsername);
        if (isLogin(mUsername)) {
            taskContext.set(GetProgramTask.KEY_TOKEN, UserManager.getInstance(mActivity).getToken());
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
    public void onDestroy() {
        mPullHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private View.OnClickListener mOnSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, SettingsActivity.class);
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
                Toast.makeText(mActivity, R.string.toast_userpage_nopic, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mActivity, R.string.toast_userpage_nopic, Toast.LENGTH_SHORT).show();
            } finally {
                mIconDialog.dismiss();
            }
        }

    };

    private View.OnClickListener mOnIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(mActivity).isLogin(mUsername)) {
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
                Dialog dialog = DialogManager.alertDeleteDialog(mActivity, title, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long pid = mPrograms.get(position).getId();

                        RemoveProgramTask task = new RemoveProgramTask();
                        task.addTaskListener(onRemoveTaskListener);

                        TaskContext taskContext = new TaskContext();
                        taskContext.set(RemoveProgramTask.KEY_TOKEN, UserManager.getInstance(mActivity).getToken());
                        taskContext.set(RemoveProgramTask.KEY_PID, pid);

                        task.execute(taskContext);

                        mPrograms.remove(position);
                        mAdapter.notifyDataSetChanged();

                        if (mPrograms.isEmpty()) {
                            mNodataText.setText(R.string.userpage_user_nodata);
                            mNoDataButton.setEnabled(true);
                            mNoDataView.setVisibility(View.VISIBLE);
                        }

                        AlarmCenter.getInstance(mActivity).deletePrelive(pid);
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
        taskContext.set(UploadIconTask.KEY_USERNAME, UserManager.getInstance(mActivity).getUsernamePlain());
        taskContext.set(UploadIconTask.KEY_ICON_PATH, imagePath);
        taskContext.set(UploadIconTask.KEY_TOKEN, UserManager.getInstance(mActivity).getToken());
        task.execute(taskContext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == SettingsActivity.RESULT_LOGOUT) {
                //                finish();
            } else if (resultCode == SettingsActivity.RESULT_NICK_CHANGED) {
                mTextNickName.setText(UserManager.getInstance(mActivity).getNickname());
            }
        } else if (requestCode == REQUEST_PICKPIC && resultCode == Activity.RESULT_OK) {
            if (!UserManager.getInstance(mActivity).isLogin(mUsername)) {
                return;
            }
            try {
                mRefreshDialog.show();
                Uri uri = data.getData();
                Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                uploadIcon(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                Toast.makeText(mActivity, R.string.toast_icon_processing, Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Column does not exist");
                Toast.makeText(mActivity, R.string.toast_icon_image_error, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = ImageUtil.getScaledBitmapLimit((Bitmap) data.getExtras().get("data"), 160);
            String filename = DirManager.getImageCachePath() + "/upload_icon.tmp";
            if (ImageUtil.bitmap2File(bitmap, filename)) {
                uploadIcon(filename);
                Toast.makeText(mActivity, R.string.toast_icon_processing, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity, R.string.toast_icon_image_error, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Task.TaskListener onIconTaskListener = new Task.BaseTaskListener() {

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mActivity, R.string.toast_icon_changed, Toast.LENGTH_SHORT).show();
            UserManager.getInstance(mActivity).setUserinfo((User) event.getContext().get(UploadIconTask.KEY_USERINFO));
            String iconUrl = UserManager.getInstance(mActivity).getIcon();
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserIcon.setImageAsync(iconUrl, R.drawable.user_icon_default);
            } else {
                //                mUserIcon.setLocalImage(R.drawable.user_icon_default, true);
            }
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mActivity, R.string.toast_icon_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mActivity, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
        }
    };

    private Task.TaskListener onRemoveTaskListener = new Task.BaseTaskListener() {

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            Toast.makeText(mActivity, R.string.toast_userpage_delete_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            Toast.makeText(mActivity, R.string.toast_userpage_delete_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Toast.makeText(mActivity, R.string.toast_userpage_delete_fail, Toast.LENGTH_SHORT).show();
        }

    };

    private Task.TaskListener onGetTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            Log.d(TAG, "onTaskFinished");

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

                mNoDataView.setVisibility(View.VISIBLE);
            } else {
                mNoDataView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Log.d(TAG, "onTaskFailed");

            mRefreshDialog.dismiss();
            mPullHandler.sendEmptyMessage(MSG_PULL_FINISH);
            mPrograms.clear();
            mAdapter.notifyDataSetChanged();
            mNodataText.setText(R.string.userpage_user_error);
            mNoDataButton.setEnabled(true);
            mNoDataButton.setOnClickListener(onErrorBtnClickListener);
            mNoDataView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgressChanged(Task sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            onTaskFailed(sender, null);
        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
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
        return UserManager.getInstance(mActivity).isLogin(username);
    }

    private void goPlayerActivity(Program program) {
        Intent intent = new Intent(mActivity, LivePlayerActivity.class);
        intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
        startActivity(intent);
    }

    private void goRecorderActivity(Program program) {
        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            Intent intent = new Intent(mActivity, LiveRecordActivity.class);
            if (program != null) {
                intent.putExtra(LiveRecordActivity.EXTRA_PROGRAM, program);
            }
            startActivity(intent);
        } else {
            Toast.makeText(mActivity, R.string.toast_version_low, Toast.LENGTH_LONG).show();
        }
    }

    static class PullHandler extends Handler {
        private WeakReference<UserPageFragment> mOuter;

        public PullHandler(UserPageFragment outer) {
            mOuter = new WeakReference<UserPageFragment>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            UserPageFragment outer = mOuter.get();
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
