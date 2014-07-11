package com.pplive.liveplatform.fragment;

import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.pplive.android.image.CircularImageView;
import com.pplive.android.pulltorefresh.PullToRefreshSwipeListView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.PersonalProgramAdapter;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.model.UserFriendCount;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.GetUserDetailInfoTask;
import com.pplive.liveplatform.core.task.user.GetUserProgramsTask;
import com.pplive.liveplatform.core.task.user.UploadIconTask;
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.MyFansActivity;
import com.pplive.liveplatform.ui.MyFollowersActivity;
import com.pplive.liveplatform.ui.SettingsActivity;
import com.pplive.liveplatform.util.DirManager;
import com.pplive.liveplatform.util.ImageUtil;
import com.pplive.liveplatform.widget.dialog.IconDialog;
import com.pplive.liveplatform.widget.dialog.RefreshDialog;

public class PersonalFragment extends Fragment {

    static final String TAG = PersonalFragment.class.getSimpleName();

    public enum UserType {
        USER, OWNER;
    }

    private final static int REQUEST_SETTINGS = 7802;

    private final static int REQUEST_CAMERA = 7803;

    private final static int REQUEST_PICKPIC = 7804;

    private Activity mActivity;

    private CircularImageView mImageUserIcon;

    private TextView mTextNickName;

    private TextView mTextFollowers;

    private TextView mTextFans;

    private ImageButton mBtnSettings;

    private PullToRefreshSwipeListView mProgramContainer;

    private PersonalProgramAdapter mAdapter;

    private View mCameraIcon;

    private IconDialog mIconDialog;

    private RefreshDialog mRefreshDialog;

    private String mUsername;

    private String mNickName;

    private String mIconUrl;

    private UserFriendCount mUserFriendCount;

    private boolean mOwner = false;

    private SwipeListViewListener mSwipeListViewListener = new SwipeListViewListener() {

        @Override
        public void onStartOpen(int position, int action, boolean right) {
            Log.d(TAG, "onStartOpen");
            mProgramContainer.closeOpenedItem();
        }

        @Override
        public void onStartClose(int position, boolean right) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onOpened(int position, boolean toRight) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onMove(int position, float x) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onListChanged() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLastListItem() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFirstListItem() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDismiss(int[] reverseSortedPositions) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onClosed(int position, boolean fromRight) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onClickFrontView(int position) {
            Log.d(TAG, "onClickFrontView");
            goPlayerActivity(mAdapter.getItem(position));
        }

        @Override
        public void onClickBackView(int position) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onChoiceStarted() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onChoiceEnded() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onChoiceChanged(int position, boolean selected) {
            // TODO Auto-generated method stub

        }

        @Override
        public int onChangeSwipeMode(int position) {
            return mOwner ? SwipeListView.SWIPE_MODE_LEFT : SwipeListView.SWIPE_MODE_NONE;
        }
    };

    private Task.TaskListener mUploadIconTaskListener = new Task.BaseTaskListener() {

        public void onTaskFinished(Task sender) {
            mRefreshDialog.dismiss();
        };

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {

            Toast.makeText(mActivity, R.string.toast_icon_changed, Toast.LENGTH_SHORT).show();

            UserManager.getInstance(mActivity).setUserinfo((User) event.getContext().get(Extra.KEY_USER_INFO));
            String iconUrl = UserManager.getInstance(mActivity).getIcon();

            if (!TextUtils.isEmpty(iconUrl)) {
                mImageUserIcon.setImageAsync(iconUrl);
            }
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Toast.makeText(mActivity, R.string.toast_icon_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            Toast.makeText(mActivity, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

    };

    private Task.TaskListener mGetUserDetailInfoTaskListener = new Task.BaseTaskListener() {

        public void onTaskFinished(Task sender) {
            mRefreshDialog.dismiss();
        };

        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {

            TaskContext context = event.getContext();

            List<Program> programs = (List<Program>) context.get(Extra.KEY_USER_PROGRAMS);

            mAdapter.refreshData(programs);

            mUserFriendCount = (UserFriendCount) context.get(Extra.KEY_USER_FRIEND_COUNT);

            updateView();
        }

    };

    private Task.TaskListener mGetUserProgramsTaskListener = new Task.BaseTaskListener() {

        public void onTaskFinished(Task sender) {
            mRefreshDialog.dismiss();

            mProgramContainer.onRefreshComplete();
        };

        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            List<Program> programs = (List<Program>) event.getContext().get(Extra.KEY_USER_PROGRAMS);

            mAdapter.refreshData(programs);
        };
    };

    private View.OnClickListener mOnSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS);
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
        View layout = inflater.inflate(R.layout.fragment_personal, container, false);

        mProgramContainer = (PullToRefreshSwipeListView) layout.findViewById(R.id.program_container);
        mAdapter = new PersonalProgramAdapter(mActivity);
        mProgramContainer.setAdapter(mAdapter);
        mProgramContainer.setSwipeListViewListener(mSwipeListViewListener);
        mProgramContainer.setOnRefreshListener(new OnRefreshListener<SwipeListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<SwipeListView> refreshView) {
                refreshData();
            }
        });

        mRefreshDialog = new RefreshDialog(mActivity);
        mIconDialog = new IconDialog(mActivity, R.style.icon_dialog);

        mBtnSettings = (ImageButton) layout.findViewById(R.id.btn_settings);
        mBtnSettings.setOnClickListener(mOnSettingsBtnClickListener);

        mTextNickName = (TextView) layout.findViewById(R.id.text_nickname);

        mTextFollowers = (TextView) layout.findViewById(R.id.text_followers);
        mTextFollowers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MyFollowersActivity.class);
                intent.putExtra(Extra.KEY_USERNAME, mUsername);
                startActivity(intent);
            }
        });
        mTextFans = (TextView) layout.findViewById(R.id.text_fans);
        mTextFans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MyFansActivity.class);
                intent.putExtra(Extra.KEY_USERNAME, mUsername);
                startActivity(intent);
            }
        });

        mImageUserIcon = (CircularImageView) layout.findViewById(R.id.image_user_icon);

        mCameraIcon = layout.findViewById(R.id.image_camera);
        mCameraIcon.setOnClickListener(mOnIconClickListener);
        //init views

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        Bundle bundle = getArguments();
        UserType type = (UserType) bundle.getSerializable(Extra.KEY_USER_TYPE);
        if (UserType.OWNER == type) {
            mUsername = UserManager.getInstance(getActivity()).getUsernamePlain();
            mIconUrl = UserManager.getInstance(getActivity()).getIcon();
            mNickName = UserManager.getInstance(getActivity()).getNickname();
        } else {
            Intent intent = getActivity().getIntent();

            mUsername = intent.getStringExtra(Extra.KEY_USERNAME);
            mIconUrl = intent.getStringExtra(Extra.KEY_ICON_URL);
            mNickName = intent.getStringExtra(Extra.KEY_NICKNAME);
        }

        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResumre");

        mProgramContainer.closeOpenedItem();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mRefreshDialog.isShowing()) {
            mRefreshDialog.dismiss();
        }

    }

    private void updateView() {
        mOwner = isLogin(mUsername);

        mCameraIcon.setVisibility(mOwner ? View.VISIBLE : View.GONE);
        mBtnSettings.setVisibility(mOwner ? View.VISIBLE : View.GONE);
        mTextNickName.setText(mNickName);
        mTextFollowers.setText(getString(R.string.fmt_followers, mUserFriendCount.getFollowsCount()));
        mTextFans.setText(getString(R.string.fmt_fans, mUserFriendCount.getFansCount()));
        mImageUserIcon.setImageAsync(mIconUrl);
    }

    private void init() {
        if (TextUtils.isEmpty(mUsername)) {
            return;
        }

        GetUserDetailInfoTask task = new GetUserDetailInfoTask();
        task.addTaskListener(mGetUserDetailInfoTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(Extra.KEY_USERNAME, mUsername);
        if (isLogin(mUsername)) {
            taskContext.set(Extra.KEY_TOKEN, UserManager.getInstance(mActivity).getToken());
        }

        task.execute(taskContext);

        mRefreshDialog.show();
    }

    private void refreshData() {
        if (TextUtils.isEmpty(mUsername)) {
            return;
        }

        GetUserProgramsTask task = new GetUserProgramsTask();
        task.addTaskListener(mGetUserProgramsTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(Extra.KEY_USERNAME, mUsername);
        if (isLogin(mUsername)) {
            taskContext.set(Extra.KEY_TOKEN, UserManager.getInstance(mActivity).getToken());
        }

        task.execute(taskContext);

        mRefreshDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void uploadIcon(String imagePath) {
        UploadIconTask task = new UploadIconTask();
        task.addTaskListener(mUploadIconTaskListener);
        TaskContext taskContext = new TaskContext();
        taskContext.set(Extra.KEY_USERNAME, UserManager.getInstance(mActivity).getUsernamePlain());
        taskContext.set(Extra.KEY_ICON_PATH, imagePath);
        taskContext.set(Extra.KEY_TOKEN, UserManager.getInstance(mActivity).getToken());
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

    private boolean isLogin(String username) {
        return UserManager.getInstance(mActivity).isLogin(username);
    }

    private void goPlayerActivity(Program program) {
        Intent intent = new Intent(mActivity, LivePlayerActivity.class);
        intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
        startActivity(intent);
    }
}
