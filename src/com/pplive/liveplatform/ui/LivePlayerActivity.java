package com.pplive.liveplatform.ui;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.service.live.model.WatchList;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.player.GetMediaTask;
import com.pplive.liveplatform.core.task.player.LiveStatusTask;
import com.pplive.liveplatform.core.task.player.PutFeedTask;
import com.pplive.liveplatform.dac.DacSender;
import com.pplive.liveplatform.dac.stat.WatchDacStat;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.net.event.EventNetworkChanged;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.ui.player.LivePlayerFragment;
import com.pplive.liveplatform.ui.widget.DetectableRelativeLayout;
import com.pplive.liveplatform.ui.widget.EnterSendEditText;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.chat.ChatBox;
import com.pplive.liveplatform.ui.widget.dialog.ShareDialog;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.ViewUtil;

import de.greenrobot.event.EventBus;

public class LivePlayerActivity extends FragmentActivity implements SensorEventListener, LivePlayerFragment.Callback {

    static final String TAG = "_LivePlayerActivity";

    private static final int SCREEN_ORIENTATION_INVALID = -1;

    private final static int MSG_LOADING_DELAY = 2000;

    private final static int MSG_MEDIA_FINISH = 2001;

    private final static int MSG_START_PLAY = 2002;

    private final static int MSG_KEEP_ALIVE = 2003;

    private final static int MSG_MEDIA_RETRY = 2004;

    private final static int LOADING_DELAY_TIME = 1 * 1000;

    private final static int KEEP_ALIVE_DELAY_TIME = 30 * 1000;

    public final static String EXTRA_PROGRAM = "program";

    private DetectableRelativeLayout mRootLayout;

    private LivePlayerFragment mLivePlayerFragment;

    private View mFragmentContainer;

    private View mCommentView;

    private View mCommentWrapper;

    private View mLoadingImage;

    private ChatBox mChatBox;

    private ShareDialog mShareDialog;

    private LoadingButton mLoadingButton;

    private Button mWriteBtn;

    private EnterSendEditText mCommentEditText;

    private SensorManager mSensorManager;

    private Sensor mSensor;

    private int mCurrentOrient;

    private int mUserOrient;

    private long mDelay;

    private boolean mIsFull;

    private boolean mWriting;

    private boolean mRotatable;

    private boolean mFirstLoading;

    private boolean mSecondLoading;

    private boolean mFirstLoadFinish;

    private boolean mSecondLoadFinish;

    private boolean mLoadDelayed;

    private boolean mInterrupted;

    private boolean mNetworkDown;

    private boolean mEnded;

    private int mHalfScreenHeight;

    private Program mProgram;

    private String mUrl;

    private Context mContext;

    private WatchDacStat mWatchDacStat;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "onCreate");
        mContext = this;
        mHandler = new InnerHandler(this);

        /* init window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_player);

        /* init fragment */
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mLivePlayerFragment = new LivePlayerFragment();
        fragmentTransaction.add(R.id.layout_player_fragment, mLivePlayerFragment);
        fragmentTransaction.commit();
        mLivePlayerFragment.setCallbackListener(this);

        mShareDialog = new ShareDialog(this, R.style.share_dialog, getString(R.string.share_dialog_title));
        mShareDialog.setActivity(this);

        /* init values */
        mDelay = 0;
        mUserOrient = SCREEN_ORIENTATION_INVALID;
        mCurrentOrient = getRequestedOrientation();
        mHalfScreenHeight = (int) (DisplayUtil.getWidthPx(this) * 3.0f / 4.0f);

        /* init views */
        mRootLayout = (DetectableRelativeLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRootLayout.setOnSoftInputListener(onSoftInputListener);
        mCommentEditText = (EnterSendEditText) findViewById(R.id.edit_player_comment);
        mCommentEditText.setOnEnterListener(onCommentEnterListener);
        mCommentView = findViewById(R.id.layout_player_comment);
        mCommentWrapper = findViewById(R.id.wrapper_player_comment);
        mFragmentContainer = findViewById(R.id.layout_player_fragment);
        mChatBox = (ChatBox) findViewById(R.id.layout_player_chatbox);
        mChatBox.setOnSingleTapListener(onSingleTapListener);
        mLoadingImage = findViewById(R.id.layout_player_loading);
        mLoadingButton = (LoadingButton) findViewById(R.id.btn_player_loading);
        mWriteBtn = (Button) findViewById(R.id.btn_player_write);
        mWriteBtn.setOnClickListener(onWriteBtnClickListener);
        setLayout(DisplayUtil.isLandscape(this), true);

        /* init others */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        setIntent(intent);
        mUrl = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mProgram = (Program) getIntent().getSerializableExtra(EXTRA_PROGRAM);
        mLivePlayerFragment.setProgram(mProgram);
        mLivePlayerFragment.setLayout(mIsFull);

        initDac();
        mWatchDacStat.setWatchType(mProgram.isVOD());
        mWatchDacStat.setProgramInfo(mProgram);

        if (TextUtils.isEmpty(mUrl)) {
            showLoading();
            mHandler.sendEmptyMessageDelayed(MSG_LOADING_DELAY, LOADING_DELAY_TIME);
            startGetMedia();
        } else {
            if (mProgram.isVOD()) {
                mLivePlayerFragment.setupVideoView(mUrl);
            } else {
                showWaiting();
                startGetMedia();
            }
        }
        mChatBox.start(mProgram.getId());
        keepAliveDelay(0);
    }

    private void startGetMedia() {
        mUrl = null;
        mEnded = false;
        mHandler.removeMessages(MSG_MEDIA_RETRY);
        long pid = mProgram.getId();
        if (pid > 0) {
            Log.d(TAG, "Start to get media url...");
            String username = UserManager.getInstance(this).getUsernamePlain();
            String token = UserManager.getInstance(this).getToken();
            GetMediaTask mediaTask = new GetMediaTask();
            mediaTask.addTaskListener(onGetMediaListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(Task.KEY_PID, pid);
            taskContext.set(Task.KEY_USERNAME, username);
            taskContext.set(Task.KEY_TOKEN, token);
            mediaTask.execute(taskContext);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mLivePlayerFragment.setCallbackListener(null);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        pauseWriting();
        EventBus.getDefault().unregister(this);
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        EventBus.getDefault().register(this);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        mChatBox.stop();
        mHandler.removeMessages(MSG_KEEP_ALIVE);
        sendDac();
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
            RelativeLayout.LayoutParams loadingLp = (RelativeLayout.LayoutParams) mLoadingButton.getLayoutParams();
            loadingLp.topMargin = mHalfScreenHeight - DisplayUtil.dp2px(mContext, 75);
            containerLp.height = mHalfScreenHeight;
            dialogLp.topMargin = mHalfScreenHeight;
            if (mFirstLoadFinish) {
                mChatBox.setVisibility(View.VISIBLE);
                if (mWriting) {
                    mCommentView.setVisibility(View.VISIBLE);
                    mWriteBtn.setVisibility(View.GONE);
                } else {
                    mCommentView.setVisibility(View.GONE);
                    mWriteBtn.setVisibility(View.VISIBLE);
                }
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
            if (UserManager.getInstance(mContext).isLoginSafely()) {
                startWriting();
            } else {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    private ChatBox.OnSingleTapListener onSingleTapListener = new ChatBox.OnSingleTapListener() {

        @Override
        public void onSingleTap() {
            pauseWriting();
        }
    };

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mCurrentOrient != requestedOrientation && mRotatable) {
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
                taskContext.set(PutFeedTask.KEY_PID, pid);
                taskContext.set(PutFeedTask.KEY_CONTENT, content);
                taskContext.set(PutFeedTask.KEY_TOKEN, token);
                postFeed(taskContext);
            }
            stopWriting();
            return true;
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
        ViewUtil.requestLayoutDelay(mCommentWrapper, 100);
    }

    private void popdownDialog() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mChatBox.getLayoutParams();
        lp.topMargin = mHalfScreenHeight;
        ViewUtil.requestLayoutDelay(mChatBox, 100);
    }

    @Override
    public void onTouchPlayer() {
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
        String imageUrl = mProgram.getRecommendCover();
        Bundle data = new Bundle();
        data.putString(ShareDialog.PARAM_TITLE, title);
        data.putString(ShareDialog.PARAM_TARGET_URL, TextUtils.isEmpty(shareUrl) ? getString(R.string.default_share_target_url) : shareUrl);
        data.putString(ShareDialog.PARAM_SUMMARY, String.format(getString(R.string.share_watch_format), title));
        data.putString(ShareDialog.PARAM_IMAGE_URL, TextUtils.isEmpty(imageUrl) ? "" : imageUrl);
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
            Toast.makeText(mContext, R.string.player_comment_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskFinished");
            Toast.makeText(mContext, R.string.player_comment_success, Toast.LENGTH_SHORT).show();
            mChatBox.refresh(0);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskFailed:" + event.getMessage());
            Toast.makeText(mContext, R.string.player_comment_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private Task.OnTaskListener onLiveStatusTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            keepAliveDelay(0);
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            LiveStatus liveStatus = (LiveStatus) event.getContext().get(LiveStatusTask.KEY_RESULT);
            mDelay = liveStatus.getDelayInSeconds();
            switch (liveStatus.getStatus()) {
            case STOPPED:
            case DELETED:
            case SYS_DELETED:
                Log.d(TAG, "Stopped!");
                mEnded = true;
                DialogManager.alertPlayEndDialog(LivePlayerActivity.this).show();
                break;
            case LIVING:
                mEnded = false;
                if (mInterrupted && !mNetworkDown) {
                    Log.d(TAG, "Interrupted, Retry...");
                    mLivePlayerFragment.showBreakInfo(getString(R.string.player_signal_break));
                    showWaiting();
                    startGetMedia();
                }
                keepAliveDelay(mDelay * 1000);
                break;
            default:
                break;
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            keepAliveDelay(KEEP_ALIVE_DELAY_TIME);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            keepAliveDelay(KEEP_ALIVE_DELAY_TIME);
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private Task.OnTaskListener onGetMediaListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "MediaTask onTimeout");
            mHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "MediaTask onTaskFinished");
            mUrl = null;
            WatchList watchList = (WatchList) event.getContext().get(GetMediaTask.KEY_RESULT);
            Watch.Protocol protocol = watchList.getRecommendedProtocol();
            if (protocol == Watch.Protocol.RTMP) {
                mUrl = watchList.getRtmpPlayURL();
            } else if (protocol == Watch.Protocol.LIVE2) {
                if (mProgram.isLiving()) {
                    mUrl = watchList.getLive2LiveM3U8PlayURL();
                } else if (mProgram.isVOD()) {
                    mUrl = watchList.getLive2VODM3U8PlayURL();
                }
            }
            Log.d(TAG, "mUrl:" + mUrl);

            mWatchDacStat.onMediaServerResponse();
            mWatchDacStat.setPlayStartTime(watchList.getNowTime());
            mWatchDacStat.setPlayProtocol(protocol);
            mWatchDacStat.setServerAddress(watchList.getRecommendedWatchAddress());

            mHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "MediaTask onTaskFailed: " + event.getMessage());
            mHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
            mWatchDacStat.onMediaServerResponse();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "MediaTask onTaskCancel");
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private void retryPlay() {
        if (!mNetworkDown) {
            mLivePlayerFragment.showBreakInfo(getString(R.string.player_play_timeout));
            showWaiting();
            startGetMedia();
        } else {
            mLivePlayerFragment.showBreakInfo(getString(R.string.player_network_break));
        }
        mHandler.sendEmptyMessageDelayed(MSG_MEDIA_RETRY, 6000);
    }

    public void showLoading() {
        mRotatable = false;
        mLoadDelayed = false;
        mFirstLoadFinish = false;
        mSecondLoadFinish = false;
        mFirstLoading = true;
        mSecondLoading = true;
        mLivePlayerFragment.initIcon();
        mLoadingImage.setVisibility(View.VISIBLE);
        mLoadingButton.startLoading(R.string.player_loading);
        mCommentWrapper.setVisibility(View.GONE);
        mChatBox.setVisibility(View.GONE);
        mWriteBtn.setVisibility(View.GONE);
    }

    public void showWaiting() {
        mLoadDelayed = true;
        mFirstLoadFinish = false;
        mSecondLoadFinish = false;
        mFirstLoading = true;
        mSecondLoading = true;
        mLivePlayerFragment.initIcon();
        mLoadingImage.setVisibility(View.GONE);
        mLoadingButton.startLoading(R.string.player_waiting);
    }

    private void checkFirstLoading() {
        if (mFirstLoadFinish && mLoadDelayed && !isFinishing() && mFirstLoading) {
            hideFirstLoading();
            if (mProgram.isPrelive()) {
                mSecondLoadFinish = true;
                hideSecondLoading();
                mLivePlayerFragment.onStartPrelive();
                mLivePlayerFragment.startTimer();
            } else {
                if (!TextUtils.isEmpty(mUrl)) {
                    mLivePlayerFragment.setupVideoView(mUrl);
                } else {
                    Toast.makeText(mContext, R.string.toast_player_error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void checkSecondLoading() {
        if (mSecondLoadFinish && !isFinishing() && mSecondLoading) {
            hideSecondLoading();
            mLivePlayerFragment.onStartPlay();
            mRotatable = true;
        }
    }

    public void hideFirstLoading() {
        mFirstLoading = false;
        mLoadingImage.setVisibility(View.GONE);
        mCommentWrapper.setVisibility(View.VISIBLE);
        mChatBox.setVisibility(View.VISIBLE);
        mWriteBtn.setVisibility(View.VISIBLE);
    }

    public void hideSecondLoading() {
        mSecondLoading = false;
        mLoadingButton.hide(true);
    }

    // Keep Alive
    private void keepAlive() {
        long pid = mProgram.getId();
        if (pid > 0 && !mEnded) {
            Log.d(TAG, "keep alive:" + System.currentTimeMillis());
            LiveStatusTask task = new LiveStatusTask();
            task.addTaskListener(onLiveStatusTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(LiveStatusTask.KEY_PID, pid);
            task.execute(taskContext);
        }
    }

    private void keepAliveDelay(long delay) {
        if (mProgram.isLiving()) {
            mHandler.removeMessages(MSG_KEEP_ALIVE);
            mHandler.sendEmptyMessageDelayed(MSG_KEEP_ALIVE, delay);
        }
    }

    // Fragment callback
    @Override
    public void onPrepare() {
        Log.d(TAG, "onPrepare");
        mInterrupted = false;
        mHandler.removeMessages(MSG_MEDIA_RETRY);
        mHandler.sendEmptyMessage(MSG_START_PLAY);
        mLivePlayerFragment.hideBreakInfo();
        mWatchDacStat.setIsSuccess(true);
        mWatchDacStat.onPlayRealStart();
    }

    @Override
    public boolean onError(int what, int extra) {
        if (mProgram.isLiving()) {
            Log.d(TAG, "onError: isLiving");
            mInterrupted = true;
            keepAliveDelay(mDelay * 1000);
            return true;
        } else if (mProgram.isVOD()) {
            Log.d(TAG, "onError: isVOD");
            mInterrupted = true;
            if (!mNetworkDown) {
                mLivePlayerFragment.showBreakInfo(getString(R.string.player_play_error));
            } else {
                mLivePlayerFragment.showBreakInfo(getString(R.string.player_network_break));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCompletion() {
        if (mProgram.isLiving()) {
            Log.d(TAG, "onCompletion: isLiving");
            mInterrupted = true;
            keepAliveDelay(0);
        } else if (mProgram.isVOD()) {
            Log.d(TAG, "onCompletion: isVOD");
            DialogManager.alertPlayEndDialog(LivePlayerActivity.this).show();
        }
    }

    @Override
    public void onReplay() {
        if (!TextUtils.isEmpty(mUrl)) {
            mLivePlayerFragment.setupVideoView(mUrl);
        }
    }

    @Override
    public void onTimeout() {
        Log.d(TAG, "onTimeout");
        retryPlay();
    }

    // Volume key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_UP:
            super.onKeyDown(keyCode, event);
            mLivePlayerFragment.syncVolume();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp");
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_UP:
            mLivePlayerFragment.syncVolume();
            return super.onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    // Dac
    private void initDac() {
        mWatchDacStat = new WatchDacStat();
        mWatchDacStat.onPlayStart();
        mWatchDacStat.setAccessType(NetworkManager.getCurrentNetworkState());
        mWatchDacStat.setSDKRunning(PPBoxUtil.isSDKRuning());
    }

    private void sendDac() {
        mWatchDacStat.onPlayStop();
        DacSender.sendProgramWatchDac(getApplicationContext(), mWatchDacStat);
    }

    @Override
    public void onSeek() {
        mWatchDacStat.onSeek();
    }

    @Override
    public void onBufferStart() {
        mWatchDacStat.onBufferStart();
    }

    @Override
    public void onBufferEnd() {
        mWatchDacStat.onBufferEnd();
    }

    //Network
    public void onEvent(EventNetworkChanged event) {
        Log.d(TAG, "state: " + event.getNetworkState());
        switch (event.getNetworkState()) {
        case WIFI:
        case UNKNOWN:
            mInterrupted = false;
            mNetworkDown = false;
            mLivePlayerFragment.showBreakInfo(getString(R.string.player_network_retry));
            showWaiting();
            startGetMedia();
            keepAliveDelay(mDelay * 1000);
            break;
        case MOBILE:
        case FAST_MOBILE:
            DialogManager.alertMobileDialog(this, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mInterrupted = false;
                    mNetworkDown = false;
                    mLivePlayerFragment.showBreakInfo(getString(R.string.player_network_retry));
                    showWaiting();
                    startGetMedia();
                    keepAliveDelay(mDelay * 1000);
                }
            }).show();
            break;
        case DISCONNECTED:
            mNetworkDown = true;
            mLivePlayerFragment.showBreakInfo(getString(R.string.player_network_break));
            break;
        default:
            break;
        }
    }

    static class InnerHandler extends Handler {
        private WeakReference<LivePlayerActivity> mOuter;

        public InnerHandler(LivePlayerActivity activity) {
            mOuter = new WeakReference<LivePlayerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LivePlayerActivity outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                case MSG_LOADING_DELAY:
                    outer.mLoadDelayed = true;
                    outer.checkFirstLoading();
                    break;
                case MSG_MEDIA_FINISH:
                    outer.mFirstLoadFinish = true;
                    outer.checkFirstLoading();
                    break;
                case MSG_START_PLAY:
                    outer.mSecondLoadFinish = true;
                    outer.checkSecondLoading();
                    break;
                case MSG_KEEP_ALIVE:
                    outer.keepAlive();
                    break;
                case MSG_MEDIA_RETRY:
                    outer.retryPlay();
                    break;
                }
            }
        }
    }
}
