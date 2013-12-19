package com.pplive.liveplatform.ui;

import java.util.List;

import org.springframework.util.CollectionUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.player.GetMediaTask;
import com.pplive.liveplatform.core.task.player.PutFeedTask;
import com.pplive.liveplatform.ui.player.LivePlayerFragment;
import com.pplive.liveplatform.ui.widget.ChatBox;
import com.pplive.liveplatform.ui.widget.DetectableRelativeLayout;
import com.pplive.liveplatform.ui.widget.EnterSendEditText;
import com.pplive.liveplatform.ui.widget.dialog.LoadingDialog;
import com.pplive.liveplatform.ui.widget.dialog.ShareDialog;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.ViewUtil;

public class LivePlayerActivity extends FragmentActivity implements SensorEventListener, LivePlayerFragment.Callback {
    static final String TAG = "_LivePlayerActivity";

    private static final int SCREEN_ORIENTATION_INVALID = -1;

    private final static int MSG_LOADING_DELAY = 2000;

    private final static int MSG_MEDIA_FINISH = 2001;

    private final static int LOADING_DELAY_TIME = 3 * 1000;

    public final static String EXTRA_PROGRAM = "program";

    private DetectableRelativeLayout mRootLayout;

    private LivePlayerFragment mLivePlayerFragment;

    private View mFragmentContainer;

    private View mCommentView;

    private View mLoadingView;

    private ChatBox mChatBox;

    private ShareDialog mShareDialog;

    private Dialog mLoadingDialog;

    private Button mWriteBtn;

    private EnterSendEditText mCommentEditText;

    private SensorManager mSensorManager;

    private Sensor mSensor;

    private int mCurrentOrient;

    private int mUserOrient;

    private boolean mIsFull;

    private boolean mWriting;

    private boolean mFirstLoading;

    private boolean mLoadingFinish;

    private boolean mLoadingDelayed;

    private int mHalfScreenHeight;

    private Program mProgram;

    private String mUrl;

    private Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "onCreate");
        this.mContext = this;

        /* init window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_player);

        /* init fragment */
        mLivePlayerFragment = new LivePlayerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_player_fragment, mLivePlayerFragment).commit();
        mShareDialog = new ShareDialog(this, R.style.share_dialog, getString(R.string.share_dialog_title));
        mShareDialog.setActivity(this);
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setOnKeyListener(onLoadingKeyListener);

        /* init values */
        mUserOrient = SCREEN_ORIENTATION_INVALID;
        mCurrentOrient = getRequestedOrientation();
        mHalfScreenHeight = (int) (DisplayUtil.getWidthPx(this) * 3.0f / 4.0f);
        mProgram = (Program) getIntent().getSerializableExtra(EXTRA_PROGRAM);

        /* init views */
        mRootLayout = (DetectableRelativeLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRootLayout.setOnSoftInputListener(onSoftInputListener);
        mCommentEditText = (EnterSendEditText) findViewById(R.id.edit_player_comment);
        mCommentEditText.setOnEnterListener(onCommentEnterListener);
        mCommentView = findViewById(R.id.layout_player_comment);
        mFragmentContainer = findViewById(R.id.layout_player_fragment);
        mChatBox = (ChatBox) findViewById(R.id.layout_player_chatbox);
        mLoadingView = findViewById(R.id.layout_player_loading);
        mWriteBtn = (Button) findViewById(R.id.btn_player_write);
        mWriteBtn.setOnClickListener(onWriteBtnClickListener);
        mChatBox.setOnTouchListener(onDialogTouchListener);
        setLayout(DisplayUtil.isLandscape(this), true);

        /* init others */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mLivePlayerFragment.setLayout(mIsFull);
        mLivePlayerFragment.setCallbackListener(this);
        mLivePlayerFragment.setTitle(mProgram.getTitle());
        long pid = mProgram.getId();
        if (mUrl == null) {
            String username = UserManager.getInstance(this).getActiveUserPlain();
            String token = UserManager.getInstance(this).getToken();
            if (pid > 0) {
                showLoading();
                mLoadingHandler.sendEmptyMessageDelayed(MSG_LOADING_DELAY, LOADING_DELAY_TIME);
                GetMediaTask mediaTask = new GetMediaTask();
                mediaTask.addTaskListener(onGetMediaListener);
                TaskContext taskContext = new TaskContext();
                taskContext.set(Task.KEY_PID, pid);
                taskContext.set(Task.KEY_USERNAME, username);
                taskContext.set(Task.KEY_TOKEN, token);
                mediaTask.execute(taskContext);
            }
        } else {
            Log.d(TAG, "onStart mUrl:" + mUrl);
            mLivePlayerFragment.setupPlayer(mUrl);
        }
        if (pid > 0) {
            mChatBox.start(pid);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        pauseWriting();
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        mChatBox.stop();
        mLivePlayerFragment.setCallbackListener(null);
        super.onStop();
    }

    private void setLayout(boolean isFull, boolean init) {
        if (mIsFull == isFull && !init) {
            return;
        }
        Log.d(TAG, "setLayout");
        mIsFull = isFull;
        RelativeLayout.LayoutParams containerLp = (RelativeLayout.LayoutParams) mFragmentContainer.getLayoutParams();
        if (mIsFull) {
            containerLp.height = LayoutParams.MATCH_PARENT;
            mChatBox.setVisibility(View.GONE);
            mCommentView.setVisibility(View.GONE);
            mWriteBtn.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            RelativeLayout.LayoutParams dialogLp = (RelativeLayout.LayoutParams) mChatBox.getLayoutParams();
            containerLp.height = mHalfScreenHeight;
            dialogLp.topMargin = mHalfScreenHeight;
            mChatBox.setVisibility(View.VISIBLE);
            if (mWriting) {
                mCommentView.setVisibility(View.VISIBLE);
                mWriteBtn.setVisibility(View.GONE);
            } else {
                mCommentView.setVisibility(View.GONE);
                mWriteBtn.setVisibility(View.VISIBLE);
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (!init) {
            mLivePlayerFragment.setLayout(mIsFull);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        setLayout(DisplayUtil.isLandscape(this), false);
    }

    private View.OnClickListener onWriteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(mContext).isLogin()) {
                startWriting();
            } else {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mCurrentOrient != requestedOrientation && !mFirstLoading) {
            Log.d(TAG, "setRequestedOrientation");
            mCurrentOrient = requestedOrientation;
            pauseWriting();
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    public void sensorOrientation(int requestedOrientation) {
        if (mUserOrient == SCREEN_ORIENTATION_INVALID || mUserOrient == requestedOrientation) {
            mUserOrient = SCREEN_ORIENTATION_INVALID;
            setRequestedOrientation(requestedOrientation);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        double gxy = Math.sqrt(ax * ax + ay * ay);
        double g = Math.sqrt(ax * ax + ay * ay + az * az);
        double cos = ay / gxy;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rady = (ax >= 0) ? Math.acos(cos) : 2 * Math.PI - Math.acos(cos);
        double radz = Math.asin(az / g);
        double degy = Math.toDegrees(rady);
        double degz = Math.toDegrees(radz);
        if (-60 < degz && degz < 60) {
            if (90 - 25 < degy && degy < 90 + 25) {
                sensorOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (360 - 25 < degy || degy < 0 + 25) {
                sensorOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (270 - 25 < degy && degy < 270 + 25) {
                sensorOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private EnterSendEditText.OnEnterListener onCommentEnterListener = new EnterSendEditText.OnEnterListener() {

        @Override
        public boolean onEnter(View v) {
            long pid = mProgram.getId();
            if (pid > 0) {
                String content = mCommentEditText.getText().toString();
                String token = UserManager.getInstance(mContext).getToken();
                TaskContext taskContext = new TaskContext();
                taskContext.set(Task.KEY_PID, pid);
                taskContext.set(PutFeedTask.KEY_CONTENT, content);
                taskContext.set(Task.KEY_TOKEN, token);
                postFeed(taskContext);
            }
            stopWriting();
            return true;
        }
    };

    private DialogInterface.OnKeyListener onLoadingKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                finish();
                return true;
            }
            return false;
        }
    };

    private DetectableRelativeLayout.OnSoftInputListener onSoftInputListener = new DetectableRelativeLayout.OnSoftInputListener() {
        @Override
        public void onSoftInputShow() {
            Log.d(TAG, "onSoftInputShow");
            mCommentView.setVisibility(View.VISIBLE);
            popupDialog();
        }

        @Override
        public void onSoftInputHide() {
            Log.d(TAG, "onSoftInputHide");
            popdownDialog();
            mCommentView.setVisibility(View.GONE);
            if (mCurrentOrient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mWriteBtn.setVisibility(View.VISIBLE);
            }
            mWriting = false;
        }
    };

    private void startWriting() {
        if (!mWriting) {
            mWriting = true;
            mWriteBtn.setVisibility(View.GONE);
            mCommentEditText.requestFocus();
        }
    }

    private void pauseWriting() {
        if (mWriting) {
            mWriting = false;
            mCommentEditText.clearFocus();
            mCommentView.setVisibility(View.GONE);
        }
    }

    private void stopWriting() {
        if (mWriting) {
            mWriting = false;
            mCommentEditText.setText("");
            mCommentEditText.clearFocus();
            mCommentView.setVisibility(View.GONE);
        }
    }

    private void popupDialog() {
        mChatBox.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mChatBox.getLayoutParams();
        lp.topMargin = mRootLayout.getHalfHeight() - DisplayUtil.dp2px(this, 170);
        ViewUtil.showLayoutDelay(mChatBox, 100);
    }

    private void popdownDialog() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mChatBox.getLayoutParams();
        lp.topMargin = mHalfScreenHeight;
        ViewUtil.requestLayoutDelay(mChatBox, 100);
    }

    @Override
    public void onTouch() {
        pauseWriting();
    }

    @Override
    public void onModeClick() {
        if (DisplayUtil.isLandscape(getApplicationContext())) {
            mUserOrient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mUserOrient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onShareClick() {
        mShareDialog.show();
        String title = StringUtil.safeString(mProgram.getTitle());
        String shareUrl = mProgram.getShareLinkUrl();
        String imageUrl = mProgram.getCover();
        Bundle data = new Bundle();
        data.putString(ShareDialog.PARAM_TITLE, title);
        data.putString(ShareDialog.PARAM_TARGET_URL, TextUtils.isEmpty(shareUrl) ? getString(R.string.default_share_target_url) : shareUrl);
        data.putString(ShareDialog.PARAM_SUMMARY, String.format(getString(R.string.share_summary_format), getString(R.string.app_name), title));
        data.putString(ShareDialog.PARAM_IMAGE_URL, TextUtils.isEmpty(imageUrl) ? getString(R.string.default_share_image_url) : imageUrl);
        mShareDialog.setData(data);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    private void postFeed(TaskContext taskContext) {
        PutFeedTask feedTask = new PutFeedTask();
        feedTask.setReturnContext(taskContext);
        feedTask.addTaskListener(onPutFeedListener);
        feedTask.execute(taskContext);
    }

    private Task.OnTaskListener onPutFeedListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTimeout");
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskFinished");
            mChatBox.refresh(0);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskFailed:" + event.getMessage());
            //TODO
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskCancel");
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private Task.OnTaskListener onGetMediaListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "MediaTask onTimeout");
            mLoadingHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mUrl = null;
            List<Watch> watchs = (List<Watch>) event.getContext().get(GetMediaTask.KEY_RESULT);
            // TODO rtmp or live2
            if (!CollectionUtils.isEmpty(watchs)) {
                for (Watch watch : watchs) {
                    if ("rtmp".equals(watch.getProtocol())) {
                        List<String> watchList = watch.getWatchStringList();
                        if (!CollectionUtils.isEmpty(watchList)) {
                            mUrl = watchList.get(0);
                            break;
                        }
                    }
                }
                if (TextUtils.isEmpty(mUrl)) {
                    List<String> watchList = watchs.get(0).getWatchStringList();
                    if (!CollectionUtils.isEmpty(watchList)) {
                        mUrl = watchList.get(0);
                    }
                }
            }

            if (!TextUtils.isEmpty(mUrl)) {
                mLivePlayerFragment.setupPlayer(mUrl);
            } else {
                Log.w(TAG, "mUrl is empty");
            }
            mLoadingHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "MediaTask onTaskFailed: " + event.getMessage());
            mLoadingHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "MediaTask onTaskCancel");
            finish();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private View.OnTouchListener onDialogTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onChatGestureDetector.onTouchEvent(event);
            return false;
        }
    };

    private GestureDetector onChatGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            pauseWriting();
            return true;
        }
    });

    public void showLoading() {
        if (!mFirstLoading) {
            mFirstLoading = true;
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingDialog.show();
        }
    }

    public void hideLoading() {
        if (mFirstLoading) {
            mFirstLoading = false;
            mLoadingView.setVisibility(View.GONE);
            mLoadingDialog.dismiss();
        }
    }

    private Handler mLoadingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_LOADING_DELAY:
                mLoadingDelayed = true;
                break;
            case MSG_MEDIA_FINISH:
                mLoadingFinish = true;
                break;
            }
            if (mLoadingFinish && mLoadingDelayed && !isFinishing() && mFirstLoading) {
                hideLoading();
            }
        }
    };

    @Override
    public void onStartPlay() {
        //TODO
    }
}
