package com.pplive.liveplatform.ui;

import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.LiveControlAPI;
import com.pplive.liveplatform.core.api.live.MediaAPI;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.live.TokenAPI;
import com.pplive.liveplatform.core.api.live.model.LiveAlive;
import com.pplive.liveplatform.core.api.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.api.live.model.Push;
import com.pplive.liveplatform.core.dac.DacReportService;
import com.pplive.liveplatform.core.dac.stat.PublishDacStat;
import com.pplive.liveplatform.core.network.NetworkManager;
import com.pplive.liveplatform.core.network.NetworkManager.NetworkState;
import com.pplive.liveplatform.core.network.QualityPreferences;
import com.pplive.liveplatform.core.network.event.EventNetworkChanged;
import com.pplive.liveplatform.core.record.CameraManager;
import com.pplive.liveplatform.core.record.MediaRecorderListener;
import com.pplive.liveplatform.core.record.MediaRecorderView;
import com.pplive.liveplatform.core.record.Quality;
import com.pplive.liveplatform.dialog.DialogManager;
import com.pplive.liveplatform.dialog.ShareDialog;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.live.FooterBarFragment;
import com.pplive.liveplatform.ui.live.event.EventProgramDeleted;
import com.pplive.liveplatform.ui.live.event.EventProgramSelected;
import com.pplive.liveplatform.ui.live.event.EventReset;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.TimeUtil;
import com.pplive.liveplatform.widget.AnimDoor;
import com.pplive.liveplatform.widget.LoadingButton;
import com.pplive.liveplatform.widget.chat.ChatBox;

import de.greenrobot.event.EventBus;

public class LiveRecordActivity extends FragmentActivity implements View.OnClickListener, Handler.Callback {

    static final String TAG = LiveRecordActivity.class.getSimpleName();

    public static final String EXTRA_PROGRAM = "extra_program";

    private static final int WHAT_RECORD_START = 9001;

    private static final int WHAT_RECORD_END = 9002;

    private static final int WHAT_LIVING_DURATION_UPDATE = 9003;

    private static final int WHAT_PRELIVE_COUNT_DOWN_UPDATE = 9005;

    private static final int WHAT_LIVE_KEEP_ALIVE = 9006;

    private static final int WHAT_INVALIDATE_DOOR = 9007;

    private static final int WHAT_OPEN_DOOR = 9010;

    private static final int WHAT_LIVE_FAILED = 9100;

    private static final int DELAY_CHAT_SHORT = 5000;

    private static final int DLEAY_CHAT_LONG = 10000;

    private static final int MAX_REPLAY_COUNT = 2;

    private Handler mInnerHandler = new Handler(this);

    // Chat
    private View mChatContainer;

    private ChatBox mChatBox;

    private ImageButton mChatButton;

    private boolean mChating = false;

    private MediaRecorderView mMediaRecorderView;

    // Media Recorder
    private ImageButton mBtnLiveRecord;

    // private ImageButton mBtnCameraChange;

    private ToggleButton mBtnFlashLight;

    private FooterBarFragment mFooterBarFragment;

    private ShareDialog mShareDialog;

    private Dialog mLoadingDialog;

    private TextView mTextLive;

    private TextView mTextLivingDuration;

    private int mLivingDuration;

    private TextView mTextPreLiveCountDown;

    private boolean mCountDown = false;

    private Button mBtnLivingShare;

    // Anim Door
    private AnimDoor mAnimDoor;

    private LoadingButton mStatusButton;

    private View mStatusButtonWrapper;

    private View mLiveButtonWrapper;

    private boolean mOpened;

    private boolean mAttached;

    private boolean mFirstPopped;

    private Quality mQuality = Quality.Normal;

    private AnimationListener openDoorListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mAnimDoor.hide();
        }
    };

    private AnimationListener moveAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mStatusButtonWrapper.setVisibility(View.GONE);
            mLiveButtonWrapper.setVisibility(View.VISIBLE);
            rotateButton();
        }
    };

    private RotateListener mRotateButtonListener = new RotateListener() {
        @Override
        public void onRotateMiddle() {
            mBtnLiveRecord.setEnabled(true);
        }
    };

    private GetPushUrlTask mGetPushUrlTask;

    private GetUnfinishedProgramTask mGetUnfinishedProgramTask;

    private KeepLiveAliveTask mKeepLiveAliveTask;

    private Program mLivingProgram;

    private String mLivingUrl;

    private int mReplayCount = 0;

    private PublishDacStat mPublishDacStat;

    private Dialog mLivingPausedAlertDialog;

    private Dialog mAlertDialog;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        mAttached = false;
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mAttached && !mOpened) {
            Log.d(TAG, "Open Door");

            mOpened = true;
            mStatusButton.startLoading();
            mInnerHandler.sendEmptyMessage(WHAT_INVALIDATE_DOOR);
            mInnerHandler.sendEmptyMessageDelayed(WHAT_OPEN_DOOR, 2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_record);

        mMediaRecorderView = (MediaRecorderView) findViewById(R.id.media_recorder_view);
        mMediaRecorderView.setMediaRecorderListener(new MediaRecorderListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");

                mReplayCount = 0;

                mPublishDacStat.setIsSuccess(true);

                if (null != mLivingProgram) {
                    if (LiveStatusEnum.INIT == mLivingProgram.getLiveStatus() || LiveStatusEnum.PAUSE == mLivingProgram.getLiveStatus()) {
                        LiveControlAPI.getInstance().updateLiveStatusByCoTokenAsync(getApplicationContext(), mLivingProgram);
                    } else if (LiveStatusEnum.LIVING == mLivingProgram.getLiveStatus()) {
                        mPublishDacStat.onPauseEnd();
                    }
                }
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError");

                mInnerHandler.sendEmptyMessage(WHAT_LIVE_FAILED);
            }
        });

        mChatButton = (ImageButton) findViewById(R.id.btn_record_chat);
        mBtnLiveRecord = (ImageButton) findViewById(R.id.btn_live_record);
        // mBtnCameraChange = (ImageButton)
        // findViewById(R.id.btn_camera_change);
        mBtnFlashLight = (ToggleButton) findViewById(R.id.btn_flash_light);

        mFooterBarFragment = (FooterBarFragment) getSupportFragmentManager().findFragmentById(R.id.footer_bar);

        mTextLive = (TextView) findViewById(R.id.text_live);
        mTextLivingDuration = (TextView) findViewById(R.id.text_living_duration);
        mTextPreLiveCountDown = (TextView) findViewById(R.id.text_prelive_count_down);
        mBtnLivingShare = (Button) findViewById(R.id.btn_living_share);
        mBtnLivingShare.setOnClickListener(mOnShareClickListener);

        mAnimDoor = (AnimDoor) findViewById(R.id.live_animdoor);
        mAnimDoor.setOpenDoorListener(openDoorListener);

        mStatusButtonWrapper = findViewById(R.id.wrapper_live_status);
        mLiveButtonWrapper = findViewById(R.id.wrapper_live_status_right);
        mStatusButton = (LoadingButton) findViewById(R.id.btn_live_status);
        mChatBox = (ChatBox) findViewById(R.id.layout_record_chatbox);
        mChatBox.setNewMessageListener(mNewMessageListener);
        mChatContainer = findViewById(R.id.layout_record_chat);

        mShareDialog = new ShareDialog(this, R.style.share_dialog, getString(R.string.share_dialog_title));
        mShareDialog.setActivity(this);

        mLoadingDialog = new Dialog(this, R.style.Theme_LoadingDialog);
        mLoadingDialog.setContentView(R.layout.layout_live_loading_dialog);
        mLoadingDialog.setCancelable(false);

        mLivingPausedAlertDialog = DialogManager.alertLivingPaused(this, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickBtnLiveRecord();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopLiving(true);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent");

        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        EventBus.getDefault().register(this);

        mFooterBarFragment.setOnShareBtnClickListener(mOnShareClickListener);

        startPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        Program program = (Program) getIntent().getSerializableExtra(EXTRA_PROGRAM);
        if (null != program) {
            mLivingProgram = program;
        }

        if (null != mLivingProgram && LiveStatusEnum.NOT_START == mLivingProgram.getLiveStatus()) {
            mFooterBarFragment.setPreLiveProgram(mLivingProgram);
            startCountDown();
        }

        if (null == mGetUnfinishedProgramTask) {
            mGetUnfinishedProgramTask = new GetUnfinishedProgramTask();
            mGetUnfinishedProgramTask.execute();
        }

        selectQuality();

    }

    private void selectQuality() {
        if (NetworkManager.isNetworkAvailable(getApplicationContext()) && NetworkState.WIFI == NetworkManager.getCurrentNetworkState()) {

            float speed = QualityPreferences.getInstance(getApplicationContext()).getSpeed();
            Quality quality = QualityPreferences.getInstance(getApplicationContext()).getQuality();

            mQuality = null != quality ? quality : Quality.selectQuality(speed);
        } else {
            mQuality = Quality.Low;
        }

        Log.d(TAG, "mQuality: " + mQuality);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        stopCountDown();

        stopLiving(false);

        stopPreview();

        EventBus.getDefault().unregister(this);
    }

    private void moveButton() {
        Animation moveAnimation = new TranslateAnimation(0.0f, DisplayUtil.getHeightPx(this) / 2.0f - DisplayUtil.dp2px(this, 47.5f), 0.0f, 0.0f);
        moveAnimation.setFillAfter(false);
        moveAnimation.setDuration(mAnimDoor.getDuration());
        moveAnimation.setInterpolator(new LinearInterpolator());
        moveAnimation.setAnimationListener(moveAnimationListener);
        mStatusButtonWrapper.startAnimation(moveAnimation);
    }

    private void rotateButton() {
        // Find the center of the container
        final float centerX = mLiveButtonWrapper.getWidth() / 2.0f;
        final float centerY = mLiveButtonWrapper.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 180, centerX, centerY, 1.0f, true);
        rotation.setDuration(350);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRotateListener(mRotateButtonListener);
        mLiveButtonWrapper.startAnimation(rotation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onBackPressed() {
        if (!mMediaRecorderView.isRecording()) {
            mFooterBarFragment.onBackPressed();
        }
    }

    public void onEvent(EventProgramSelected event) {
        final Program program = event.getObject();

        mLivingProgram = program;

        startCountDown();
    }

    public void onEvent(EventProgramDeleted event) {
        final Program program = event.getObject();

        if (null != mLivingProgram && mLivingProgram.getId() == program.getId()) {
            mLivingProgram = null;

            stopCountDown();
        }
    }

    public void onEvent(EventReset event) {
        mLivingProgram = null;

        stopCountDown();
    }

    public void onEvent(EventNetworkChanged event) {
        Log.d(TAG, "state: " + event.getNetworkState());

        if (null != mAlertDialog) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        NetworkState state = event.getNetworkState();
        switch (state) {
        case MOBILE:
            mAlertDialog = DialogManager.alertMobile2GLive(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            break;
        case FAST_MOBILE:
            mAlertDialog = DialogManager.alertMobile3GPlay(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startLiving();
                }
            });
            break;
        case DISCONNECTED:
            mAlertDialog = DialogManager.alertNoNetworkDialog(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            break;
        default:
            break;
        }

        mAlertDialog.show();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case WHAT_RECORD_START:
            onRecordStart();
            break;
        case WHAT_RECORD_END:
            onRecordEnd();
            break;
        case WHAT_LIVING_DURATION_UPDATE:
            onLivingDurationUpdate();
            break;
        case WHAT_PRELIVE_COUNT_DOWN_UPDATE:
            onPreLiveCountDownUpdate();
            break;
        case WHAT_LIVE_KEEP_ALIVE:
            onKeepLiveAlive();
            break;
        case WHAT_OPEN_DOOR:
            onOpenDoor();
            break;
        case WHAT_INVALIDATE_DOOR:
            onInvalidateDoor();
            break;
        case WHAT_LIVE_FAILED:
            onLiveFailed();
            break;
        default:
            break;
        }

        return false;
    }

    private void onRecordStart() {
        if (null != mLivingProgram) {
            mTextLive.setVisibility(View.VISIBLE);
            mTextLivingDuration.setVisibility(View.VISIBLE);

            mChatContainer.setVisibility(View.VISIBLE);

            mBtnLivingShare.setVisibility(View.VISIBLE);

            mBtnLivingShare.setText(mLivingProgram.getTitle());

            Message msg = mInnerHandler.obtainMessage(WHAT_LIVING_DURATION_UPDATE);
            mInnerHandler.sendMessage(msg);
        }
    }

    private void onRecordEnd() {
        mChatBox.stop();
        mChatContainer.setVisibility(View.GONE);

        mTextLive.setVisibility(View.GONE);
        mTextLivingDuration.setVisibility(View.GONE);
    }

    private void onLivingDurationUpdate() {

        if (mMediaRecorderView.isRecording()) {
            mTextLivingDuration.setText(TimeUtil.stringForTimeHour(mLivingDuration++ * 1000));

            Message msg = mInnerHandler.obtainMessage(WHAT_LIVING_DURATION_UPDATE);
            mInnerHandler.sendMessageDelayed(msg, 1000 /* milliseconds */);
        }
    }

    private void onPreLiveCountDownUpdate() {
        if (null != mLivingProgram) {
            long now = System.currentTimeMillis();
            long start = mLivingProgram.getStartTime();

            Log.d(TAG, "now: " + now + "; start: " + start);

            String coming = null;
            if (start - now >= 0) {
                coming = TimeUtil.stringForTimeHour(start - now);

                if (mCountDown) {
                    mInnerHandler.sendEmptyMessageDelayed(WHAT_PRELIVE_COUNT_DOWN_UPDATE, 1000 /* milliseconds */);
                }

            } else {
                coming = TimeUtil.stringForTimeHour(0);
            }

            mTextPreLiveCountDown.setText(coming);
        }
    }

    private void onKeepLiveAlive() {
        if (null == mKeepLiveAliveTask) {
            mKeepLiveAliveTask = new KeepLiveAliveTask();

            mKeepLiveAliveTask.execute(mLivingProgram);
        }
    }

    private void onOpenDoor() {
        mStatusButton.finishLoading();
        mInnerHandler.removeMessages(WHAT_INVALIDATE_DOOR);
        moveButton();
        mAnimDoor.open();
    }

    private void onLiveFailed() {
        Log.d(TAG, "onLiveFailed");

        if (++mReplayCount > MAX_REPLAY_COUNT) {
            mAlertDialog = mLivingPausedAlertDialog;
            mAlertDialog.show();

            mReplayCount = 0;
        } else {

            if (null != mPublishDacStat) {
                mPublishDacStat.addReplayCount();
            }

            if (NetworkManager.isNetworkAvailable(getApplicationContext())) {
                onClickBtnLiveRecord();
            }
        }
    }

    private void onInvalidateDoor() {
        mAnimDoor.invalidate();
        mInnerHandler.sendEmptyMessageDelayed(WHAT_INVALIDATE_DOOR, 1000);
    }

    private void startPreview() {
        Log.d(TAG, "startPreview");

        mMediaRecorderView.startPreview();
    }

    private void stopPreview() {
        Log.d(TAG, "stopPreview");

        mMediaRecorderView.stopPreview();
    }

    private void startChating() {
        mChating = true;
        mChatButton.setSelected(true);
        mChatBox.setVisibility(View.VISIBLE);
        mChatBox.setDelay(DELAY_CHAT_SHORT, DELAY_CHAT_SHORT);
        mChatBox.refresh(0);
    }

    private void stopChating() {
        mChating = false;
        mChatBox.setVisibility(View.GONE);
        mChatBox.setDelay(DLEAY_CHAT_LONG, DLEAY_CHAT_LONG);
        mChatButton.setSelected(false);
    }

    private void startCountDown() {
        mCountDown = true;

        mTextPreLiveCountDown.setVisibility(View.VISIBLE);
        mInnerHandler.sendEmptyMessage(WHAT_PRELIVE_COUNT_DOWN_UPDATE);
    }

    private void stopCountDown() {
        mCountDown = false;

        mTextPreLiveCountDown.setVisibility(View.GONE);
        mInnerHandler.removeMessages(WHAT_PRELIVE_COUNT_DOWN_UPDATE);
    }

    private void startRecording() {
        if (TextUtils.isEmpty(mLivingUrl)) {
            return;
        }

        if (mCountDown) {
            stopCountDown();
        }

        mMediaRecorderView.setOutputPath(mLivingUrl);
        mMediaRecorderView.setQuality(mQuality);
        mMediaRecorderView.startRecording();

        if (null != mPublishDacStat) {
            mPublishDacStat.onPlayRealStart();
            obtainCodecParams();
        }

        mChatBox.setDelay(DLEAY_CHAT_LONG, DLEAY_CHAT_LONG);
        mChatBox.start(mLivingProgram.getId());

        mInnerHandler.sendEmptyMessage(WHAT_RECORD_START);
        mInnerHandler.sendEmptyMessage(WHAT_LIVE_KEEP_ALIVE);

        mBtnLiveRecord.setSelected(true);
        StateListDrawable drawable = (StateListDrawable) mBtnLiveRecord.getDrawable();
        AnimationDrawable animate = (AnimationDrawable) drawable.getCurrent();
        animate.start();
    }

    private void obtainCodecParams() {
        Camera.Size size = mMediaRecorderView.getPreviewSize();
        if (null != size) {
            mPublishDacStat.setVideoResolution(size.height, size.width);
        }

        mPublishDacStat.setBitrate(mQuality.getBitrate() / 1000);
        mPublishDacStat.setVideoFPS(mQuality.getFrameRate());
    }

    private void stopRecording(boolean stopLiving) {

        mMediaRecorderView.stopRecording();

        if (stopLiving) {
            stopLivingProgram();
            mLivingDuration = 0;

            sendDac();
        } else {

            if (null != mPublishDacStat) {
                mPublishDacStat.onPauseStart();
            }

            if (null != mLivingProgram) {
                LiveControlAPI.getInstance().updateLiveStatusByCoTokenAsync(getApplicationContext(), mLivingProgram, LiveStatusEnum.PAUSE);
            }
        }

        mLivingUrl = null;
        mBtnLiveRecord.setSelected(false);
        mInnerHandler.sendEmptyMessage(WHAT_RECORD_END);
    }

    private void stopLivingProgram() {
        if (null != mLivingProgram) {
            stopLivingProgram(mLivingProgram);
            mLivingProgram = null;
        }
    }

    private void stopLivingProgram(final Program program) {
        if (null != program && LiveStatusEnum.LIVING == program.getLiveStatus() || LiveStatusEnum.PAUSE == program.getLiveStatus()) {
            LiveControlAPI.getInstance().updateLiveStatusByCoTokenAsync(getApplicationContext(), program, LiveStatusEnum.STOPPED);
        }
    }

    private void startLiving() {
        mLoadingDialog.show();

        stopCountDown();
        mFooterBarFragment.onLivingStart();

        initDac();

        if (null == mGetPushUrlTask) {
            mGetPushUrlTask = new GetPushUrlTask();
            mGetPushUrlTask.execute(mLivingProgram);
        }
    }

    private void stopLiving(boolean stopLiving) {
        Log.d(TAG, "stopLiving: " + stopLiving);
        stopRecording(stopLiving);
        stopChating();

        mBtnLivingShare.setVisibility(View.GONE);
        mFooterBarFragment.onLivingStop();
    }

    private void initDac() {
        if (null == mPublishDacStat) {
            mPublishDacStat = new PublishDacStat();
        }

        mPublishDacStat.onPlayStart();
        mPublishDacStat.setSDKRunning(PPBoxUtil.isSDKRuning());
        mPublishDacStat.setAccessType(NetworkManager.getCurrentNetworkState());
    }

    private void sendDac() {
        if (null != mPublishDacStat) {
            mPublishDacStat.onPlayStop();
            DacReportService.sendProgramPublishDac(getApplicationContext(), mPublishDacStat);
            mPublishDacStat = null;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
        case R.id.btn_camera_change:
            onClickBtnCameraChange();
            break;
        case R.id.btn_live_record:
            onClickBtnLiveRecord();
            break;
        case R.id.btn_flash_light:
            onClickBtnFlashLight();
            break;
        case R.id.btn_record_chat:
            onClickBtnChat();
            break;
        default:
            break;
        }
    }

    private void onClickBtnCameraChange() {

        mMediaRecorderView.changeCamera();

        if (CameraManager.CAMERA_FACING_FRONT == mMediaRecorderView.getCurrentCameraId()) {
            mBtnFlashLight.setVisibility(View.GONE);
        } else {
            mBtnFlashLight.setChecked(false);
            mBtnFlashLight.setVisibility(View.VISIBLE);
        }
    }

    private void onClickBtnLiveRecord() {
        if (!mMediaRecorderView.isRecording()) {
            if (checkNetworkState()) {
                return;
            }

            startLiving();
        } else {
            stopLiving(true);
        }
    }

    private boolean checkNetworkState() {
        Log.d(TAG, "checkNetworkState");
        switch (NetworkManager.getCurrentNetworkState()) {
        case WIFI:
            break;
        case UNKNOWN:
            break;
        case FAST_MOBILE:
            DialogManager.alertMobile3GLive(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    startLiving();
                }
            }).show();

            return true;
        case MOBILE:
            DialogManager.alertMobile2GLive(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    startLiving();
                }
            }).show();

            return true;
        case DISCONNECTED:
            DialogManager.alertNoNetworkLive(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();

            return true;
        default:
            break;
        }

        return false;
    }

    private void onClickBtnFlashLight() {
        boolean isFlashOn = mBtnFlashLight.isChecked();

        mMediaRecorderView.setFlashMode(isFlashOn);

        mBtnFlashLight.setChecked(mMediaRecorderView.isFlashOn());
    }

    private void onClickBtnChat() {
        if (!mChating) {
            startChating();
        } else {
            stopChating();
        }
    }

    class GetPushUrlTask extends AsyncTask<Program, Void, String> {

        private String mLiveTitle;

        @Override
        protected void onPreExecute() {
            if (null != mFooterBarFragment) {
                mLiveTitle = mFooterBarFragment.getLiveTitle();
            }
        }

        @Override
        protected String doInBackground(Program... params) {
            String username = UserManager.getInstance(getApplicationContext()).getUsernamePlain();
            String usertoken = UserManager.getInstance(getApplicationContext()).getToken();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(usertoken)) {
                return null;
            }

            Program program = params[0];

            mPublishDacStat.setPublishStyle(null != program);

            if (null == program) {
                Log.d(TAG, "create program");

                program = new Program(username, mLiveTitle, System.currentTimeMillis());
                try {
                    program = ProgramAPI.getInstance().createProgram(usertoken, program);
                    mLivingProgram = program;
                } catch (LiveHttpException e) {
                    Log.w(TAG, e.toString());
                    program = null;
                }
            } else {
                Log.d(TAG, "has program");
            }

            if (null == program) {
                return null;
            }

            mPublishDacStat.setProgramInfo(program);

            try {
                String liveToken = program.getLiveToken();
                if (TextUtils.isEmpty(liveToken)) {
                    Log.d(TAG, "getLiveToken");
                    liveToken = TokenAPI.getInstance().getLiveToken(usertoken, program.getId(), username);
                }

                if (LiveStatusEnum.NOT_START == program.getLiveStatus()) {
                    LiveControlAPI.getInstance().updateLiveStatusByLiveToken(liveToken, program);
                }

                if (LiveStatusEnum.PAUSE == program.getLiveStatus()) {
                    LiveControlAPI.getInstance().updateLiveStatusByLiveToken(liveToken, program);
                }

                Log.d(TAG, "status: " + mLivingProgram.getLiveStatus());

                Push push = MediaAPI.getInstance().getPushByLiveToken(program.getId(), liveToken);

                mPublishDacStat.setPlayStartTime(push.getNowTime());
                mPublishDacStat.setServerAddress(push.getAddress());
                mPublishDacStat.onMediaServerResponse();

                return push.getPushUrl();
            } catch (LiveHttpException e) {
                Log.w(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }

            mGetPushUrlTask = null;

            if (StringUtil.isNullOrEmpty(url)) {
                stopLiving(true);

                Toast.makeText(LiveRecordActivity.this, R.string.toast_live_init_fail, Toast.LENGTH_SHORT).show();

                return;
            }

            mLivingUrl = url;

            startRecording();

            //            AlarmCenter.getInstance(getApplicationContext()).startPrelive(mLivingProgram.getId());
        }
    }

    class GetUnfinishedProgramTask extends AsyncTask<Void, Void, Program> {

        @Override
        protected Program doInBackground(Void... arg0) {
            String username = UserManager.getInstance(getApplicationContext()).getUsernamePlain();
            String token = UserManager.getInstance(getApplicationContext()).getToken();

            try {
                List<Program> programs = ProgramAPI.getInstance().getUnfinishedPrograms(token, username);

                if (null != programs && programs.size() > 0) {
                    return programs.get(0);
                }
            } catch (LiveHttpException e) {
                Log.w(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Program program) {
            mGetUnfinishedProgramTask = null;

            if (null == program) {
                return;
            }

            if (!mMediaRecorderView.isRecording()) {
                mLivingProgram = program;

                mAlertDialog = mLivingPausedAlertDialog;
                mAlertDialog.show();
            }
        }
    }

    class KeepLiveAliveTask extends AsyncTask<Program, Void, LiveAlive> {

        @Override
        protected LiveAlive doInBackground(Program... params) {
            String coToken = UserManager.getInstance(getApplicationContext()).getToken();

            Program program = params[0];

            if (null != program && !TextUtils.isEmpty(coToken)) {
                try {
                    LiveAlive liveAlive = LiveControlAPI.getInstance().keepLiveAlive(coToken, program.getId());

                    return liveAlive;
                } catch (LiveHttpException e) {

                    Log.w(TAG, "keep alive failed.");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(LiveAlive result) {
            mKeepLiveAliveTask = null;

            long delay = 60; // second
            if (null != result) {
                delay = result.getDelayInSeconds() /* second */;
            }

            if (mMediaRecorderView.isRecording()) {
                mInnerHandler.sendEmptyMessageDelayed(WHAT_LIVE_KEEP_ALIVE, delay * 1000 /* millisecond */);
            }
        }
    }

    private ChatBox.INewMessageListener mNewMessageListener = new ChatBox.INewMessageListener() {

        @Override
        public void notifyMessage() {
            if (!mFirstPopped) {
                startChating();
            }
            mFirstPopped = true;
        }
    };

    private View.OnClickListener mOnShareClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mShareDialog.show();
            Bundle data = new Bundle();
            if (mLivingProgram != null) {
                String title = StringUtil.safeString(mLivingProgram.getTitle());
                String shareUrl = mLivingProgram.getShareLinkUrl();
                String imageUrl = mLivingProgram.getRecommendCover();
                String summary = String.format(getString(R.string.share_record_format), title);
                data.putString(ShareDialog.PARAM_TITLE, title);
                data.putString(ShareDialog.PARAM_TARGET_URL, TextUtils.isEmpty(shareUrl) ? getString(R.string.default_share_target_url) : shareUrl);
                data.putString(ShareDialog.PARAM_SUMMARY, summary);
                data.putString(ShareDialog.PARAM_IMAGE_URL, TextUtils.isEmpty(imageUrl) ? "" : imageUrl);
            } else {
                String title = getString(R.string.share_record_default_title);
                String shareUrl = getString(R.string.default_share_target_url);
                String imageUrl = "";
                String summary = getString(R.string.share_record_default_text);
                data.putString(ShareDialog.PARAM_TITLE, title);
                data.putString(ShareDialog.PARAM_TARGET_URL, shareUrl);
                data.putString(ShareDialog.PARAM_SUMMARY, summary);
                data.putString(ShareDialog.PARAM_IMAGE_URL, imageUrl);
            }
            mShareDialog.setData(data);
        }
    };
}