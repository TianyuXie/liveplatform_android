package com.pplive.liveplatform.ui;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.LiveControlService;
import com.pplive.liveplatform.core.service.live.MediaService;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.TokenService;
import com.pplive.liveplatform.core.service.live.model.LiveAlive;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.Push;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.net.event.EventNetworkChanged;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.ui.record.FooterBarFragment;
import com.pplive.liveplatform.ui.record.LiveMediaRecoder;
import com.pplive.liveplatform.ui.record.MediaRecorderView;
import com.pplive.liveplatform.ui.record.event.EventProgramDeleted;
import com.pplive.liveplatform.ui.record.event.EventProgramSelected;
import com.pplive.liveplatform.ui.record.event.EventReset;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.ChatBox;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.dialog.ShareDialog;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.TimeUtil;

import de.greenrobot.event.EventBus;

public class LiveRecordActivity extends FragmentActivity implements View.OnClickListener, Handler.Callback {

    static final String TAG = "_LiveRecordActivity";

    public static final String EXTRA_PROGRAM = "extra_program";

    private static final int WHAT_RECORD_START = 9001;
    private static final int WHAT_RECORD_END = 9002;
    private static final int WHAT_RECORD_UPDATE = 9003;

    private static final int WHAT_LIVE_COMING_UPDATE = 9005;

    private static final int WHAT_LIVE_KEEP_ALIVE = 9006;

    private static final int WHAT_OPEN_DOOR = 9010;

    private static final int WHAT_LIVE_FAILED = 9100;

    private static final int CHAT_SHORT_DELAY = 5000;

    private static final int CHAT_LONG_DELAY = 10000;

    private Handler mInnerHandler = new Handler(this);

    private boolean mChating = false;
    private View mChatContainer;
    private ChatBox mChatBox;
    private ImageButton mChatButton;

    private MediaRecorderView mMediaRecorderView;

    private ImageButton mBtnLiveRecord;
    private ImageButton mBtnCameraChange;
    private ToggleButton mBtnFlashLight;

    private FooterBarFragment mFooterBarFragment;

    private ShareDialog mShareDialog;

    private TextView mTextLive;
    private TextView mTextRecordDuration;
    private TextView mTextLiveComing;
    private TextView mTextLivingTitle;

    private ImageButton mBtnLivingShare;

    private boolean mCountDown = false;

    private AnimDoor mAnimDoor;
    private LoadingButton mStatusButton;
    private View mStatusButtonWrapper;
    private View mLiveButtonWrapper;

    private boolean mOpened;
    private boolean mAttached;

    private boolean mFirstPopped;

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

    private GetPushUrlTask mGetPushUrlOneStepTask;
    private GetUserLivingTask mGetUserLivingTask;
    private KeepLiveAliveTask mKeepLiveAliveTask;

    private Program mLivingProgram;
    private String mLivingUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_record);

        mMediaRecorderView = (MediaRecorderView) findViewById(R.id.media_recorder_view);
        mMediaRecorderView.setOnErrorListener(new LiveMediaRecoder.OnErrorListener() {

            @Override
            public void onError() {
                Log.d(TAG, "onError");

                if (mMediaRecorderView.isRecording()) {
                    mInnerHandler.sendEmptyMessage(WHAT_LIVE_FAILED);
                }
            }
        });

        mChatButton = (ImageButton) findViewById(R.id.btn_record_chat);
        mBtnLiveRecord = (ImageButton) findViewById(R.id.btn_live_record);
        mBtnCameraChange = (ImageButton) findViewById(R.id.btn_camera_change);
        mBtnFlashLight = (ToggleButton) findViewById(R.id.btn_flash_light);

        mFooterBarFragment = (FooterBarFragment) getSupportFragmentManager().findFragmentById(R.id.footer_bar);

        mTextLive = (TextView) findViewById(R.id.text_live);
        mTextRecordDuration = (TextView) findViewById(R.id.text_record_duration);
        mTextLiveComing = (TextView) findViewById(R.id.text_live_coming);
        mTextLivingTitle = (TextView) findViewById(R.id.text_living_title);
        mBtnLivingShare = (ImageButton) findViewById(R.id.btn_living_share);
        mBtnLivingShare.setOnClickListener(mOnShareClickListener);

        mAnimDoor = (AnimDoor) findViewById(R.id.live_animdoor);
        mAnimDoor.setOpenDoorListener(openDoorListener);

        mStatusButtonWrapper = findViewById(R.id.wrapper_live_status);
        mLiveButtonWrapper = findViewById(R.id.wrapper_live_status_right);
        mStatusButton = (LoadingButton) findViewById(R.id.btn_live_status);
        mChatBox = (ChatBox) findViewById(R.id.layout_record_chatbox);
        mChatBox.setNewMessageListener(newMessageListener);
        mChatContainer = findViewById(R.id.layout_record_chat);

        mShareDialog = new ShareDialog(this, R.style.share_dialog, getString(R.string.share_dialog_title));
        mShareDialog.setActivity(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");

        super.onStart();

        EventBus.getDefault().register(this);

        mFooterBarFragment.setOnShareBtnClickListener(mOnShareClickListener);

        startPreview();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        if (null != mLivingProgram && LiveStatusEnum.NOT_START == mLivingProgram.getLiveStatus()) {
            startCountDown();
        }

        if (null == mGetUserLivingTask) {
            mGetUserLivingTask = new GetUserLivingTask();
            mGetUserLivingTask.execute();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");

        super.onStop();

        stopCountDown();

        stopRecording(false);

        stopPreview();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

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
            mInnerHandler.sendEmptyMessageDelayed(WHAT_OPEN_DOOR, 2000);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onBackPressed() {
        if (!mFooterBarFragment.isHidden()) {
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

        switch (event.getNetworkState()) {
        case MOBILE:
        case THIRD_GENERATION:
            DialogManager.alertMobileDialog(this, null).show();
            break;

        default:
            break;
        }
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
        case WHAT_RECORD_UPDATE:
            onRecordUpdate(msg.arg1);
            break;
        case WHAT_LIVE_COMING_UPDATE:
            onLiveComingUpdate();
            break;
        case WHAT_LIVE_KEEP_ALIVE:
            onKeepLiveAlive();
            break;
        case WHAT_OPEN_DOOR:
            onOpenDoor();
            break;
        case WHAT_LIVE_FAILED:
            onLiveFailed();
            break;
        default:
            break;
        }

        return false;
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
        rotation.setRotateListener(rotateButtonListener);
        mLiveButtonWrapper.startAnimation(rotation);
    }

    private RotateListener rotateButtonListener = new RotateListener() {
        @Override
        public void onRotateMiddle() {
            mBtnLiveRecord.setBackgroundResource(R.drawable.live_record_btn_live_record);
        }
    };

    private void onRecordStart() {
        if (null != mLivingProgram) {
            mTextLive.setVisibility(View.VISIBLE);
            mTextRecordDuration.setVisibility(View.VISIBLE);

            mChatContainer.setVisibility(View.VISIBLE);

            mBtnLivingShare.setVisibility(View.VISIBLE);

            mTextLivingTitle.setVisibility(View.VISIBLE);
            mTextLivingTitle.setText(mLivingProgram.getTitle());

            // TODO: Debug Code
            mTextLivingTitle.append("\n");
            mTextLivingTitle.append("pid: " + mLivingProgram.getId());
            mTextLivingTitle.append("\n");
            mTextLivingTitle.append(mLivingUrl);

            Message msg = mInnerHandler.obtainMessage(WHAT_RECORD_UPDATE);
            mInnerHandler.sendMessage(msg);
        }
    }

    private void onRecordEnd() {
        mChatBox.stop();
        mChatContainer.setVisibility(View.GONE);

        mTextLive.setVisibility(View.GONE);
        mTextRecordDuration.setVisibility(View.GONE);
    }

    private void onRecordUpdate(int duration) {

        if (mMediaRecorderView.isRecording()) {
            mTextRecordDuration.setText(TimeUtil.stringForTimeHour(duration * 1000));

            Message msg = mInnerHandler.obtainMessage(WHAT_RECORD_UPDATE, duration + 1 /* arg1 */, 0 /* arg2 */);
            mInnerHandler.sendMessageDelayed(msg, 1000 /* milliseconds */);
        }
    }

    private void onLiveComingUpdate() {
        if (null != mLivingProgram) {
            long now = System.currentTimeMillis();
            long start = mLivingProgram.getStartTime();

            Log.d(TAG, "now: " + now + "; start: " + start);

            String coming = null;
            if (start - now >= 0) {
                coming = TimeUtil.stringForTimeHour(start - now);

                if (mCountDown) {
                    mInnerHandler.sendEmptyMessageDelayed(WHAT_LIVE_COMING_UPDATE, 1000 /* milliseconds */);
                }

            } else {
                coming = TimeUtil.stringForTimeHour(0);
            }

            mTextLiveComing.setText(coming);
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
        moveButton();
        mAnimDoor.open();
    }

    private void onLiveFailed() {
        DialogManager.alertLivingTerminated(LiveRecordActivity.this, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickBtnLiveRecord();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = UserManager.getInstance(getApplicationContext()).getUsernamePlain();
                String coToken = UserManager.getInstance(getApplicationContext()).getToken();

                LiveControlService.getInstance().updateLiveStatusByCoTokenAsync(coToken, mLivingProgram.getId(), LiveStatusEnum.STOPPED, username);

                performOnClickStopRecording();
            }
        }).show();
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
        mChatBox.setDelay(CHAT_SHORT_DELAY, CHAT_SHORT_DELAY);
        mChatBox.refresh(0);
    }

    private void stopChating() {
        mChating = false;
        mChatBox.setVisibility(View.GONE);
        mChatBox.setDelay(CHAT_LONG_DELAY, CHAT_LONG_DELAY);
        mChatButton.setSelected(false);
    }

    private void startCountDown() {
        mCountDown = true;

        mTextLiveComing.setVisibility(View.VISIBLE);
        mInnerHandler.sendEmptyMessage(WHAT_LIVE_COMING_UPDATE);
    }

    private void stopCountDown() {
        mCountDown = false;

        mTextLiveComing.setVisibility(View.GONE);
        mInnerHandler.removeMessages(WHAT_LIVE_COMING_UPDATE);
    }

    private void startRecording() {
        if (TextUtils.isEmpty(mLivingUrl)) {
            return;
        }

        if (mCountDown) {
            stopCountDown();
        }

        mMediaRecorderView.setOutputPath(mLivingUrl);
        mMediaRecorderView.startRecording();

        mInnerHandler.sendEmptyMessage(WHAT_RECORD_START);
        mInnerHandler.sendEmptyMessage(WHAT_LIVE_KEEP_ALIVE);

        mChatBox.setDelay(CHAT_LONG_DELAY, CHAT_LONG_DELAY);
        mChatBox.start(mLivingProgram.getId());
    }

    private void stopRecording(boolean stopLive) {
        if (mMediaRecorderView.isRecording()) {

            mMediaRecorderView.stopRecording();

            if (stopLive) {
                stopLiving(mLivingProgram);
            }

            mLivingUrl = null;
            mLivingProgram = null;
            mBtnLiveRecord.setSelected(mMediaRecorderView.isRecording());
            mInnerHandler.sendEmptyMessage(WHAT_RECORD_END);
        }
    }

    private void stopLiving(Program program) {
        String username = UserManager.getInstance(getApplicationContext()).getUsernamePlain();
        String coToken = UserManager.getInstance(getApplicationContext()).getToken();

        LiveControlService.getInstance().updateLiveStatusByCoTokenAsync(coToken, program.getId(), LiveStatusEnum.STOPPED, username);
        program.setLiveStatus(LiveStatusEnum.STOPPED);
    }

    private void performOnClickStartRecording() {
        mTextLiveComing.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().hide(mFooterBarFragment).commit();
        if (null == mGetPushUrlOneStepTask) {
            mGetPushUrlOneStepTask = new GetPushUrlTask();
            mGetPushUrlOneStepTask.execute(mLivingProgram);
        }
    }

    private void performOnClickStopRecording() {
        mTextLivingTitle.setVisibility(View.GONE);
        mBtnLivingShare.setVisibility(View.GONE);

        stopRecording(true);
        stopChating();
        getSupportFragmentManager().beginTransaction().show(mFooterBarFragment).commit();
        mFooterBarFragment.reset();
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
    }

    private void onClickBtnLiveRecord() {
        switch (NetworkManager.getCurrentNetworkState()) {
        case WIFI:
        case UNKNOWN:
            break;
        case THIRD_GENERATION:
        case MOBILE:
            DialogManager.alertMobileDialog(this, null).show();
            break;
        case DISCONNECTED:
            DialogManager.alertNoNetworkDialog(this, null).show();
            break;
        default:
            break;
        }

        if (!mMediaRecorderView.isRecording()) {
            performOnClickStartRecording();
        } else {
            performOnClickStopRecording();
        }
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
            if (null == program) {
                Log.d(TAG, "create program");

                program = new Program(username, mLiveTitle, System.currentTimeMillis());
                try {
                    program = ProgramService.getInstance().createProgram(usertoken, program);
                    mLivingProgram = program;
                } catch (LiveHttpException e) {
                    Log.w(TAG, e.toString());
                }
            } else {
                Log.d(TAG, "has program");

            }

            if (null == program) {

                return null;
            }

            try {
                String liveToken = program.getLiveToken();
                if (TextUtils.isEmpty(liveToken)) {
                    Log.d(TAG, "getLiveToken");
                    liveToken = TokenService.getInstance().getLiveToken(usertoken, program.getId(), username);
                }

                if (LiveStatusEnum.LIVING == program.getLiveStatus()) {

                } else {
                    LiveControlService.getInstance().updateLiveStatusByLiveToken(liveToken, program.getId(), LiveStatusEnum.INIT);
                    program.setLiveStatus(LiveStatusEnum.INIT);

                    LiveControlService.getInstance().updateLiveStatusByLiveToken(liveToken, program.getId(), LiveStatusEnum.PREVIEW);
                    program.setLiveStatus(LiveStatusEnum.PREVIEW);

                    LiveControlService.getInstance().updateLiveStatusByLiveToken(liveToken, program.getId(), LiveStatusEnum.LIVING);
                    program.setLiveStatus(LiveStatusEnum.LIVING);

                    Log.d(TAG, "status: " + mLivingProgram.getLiveStatus());
                }

                Push push = MediaService.getInstance().getPushByLiveToken(program.getId(), liveToken);

                String url = null;
                for (int i = 0, len = push.getPushUrlList().size(); i < len; ++i) {
                    url = push.getPushUrlList().get(i);
                    if (!TextUtils.isEmpty(url)) {
                        break;
                    }
                }

                return url;
            } catch (LiveHttpException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            mGetPushUrlOneStepTask = null;

            if (StringUtil.isNullOrEmpty(url)) {
                performOnClickStopRecording();
                return;
            }

            mLivingUrl = url;
            startRecording();

            mBtnLiveRecord.setSelected(mMediaRecorderView.isRecording());
        }
    }

    class GetUserLivingTask extends AsyncTask<Void, Void, Program> {

        @Override
        protected Program doInBackground(Void... arg0) {
            String username = UserManager.getInstance(getApplicationContext()).getUsernamePlain();

            try {
                List<Program> programs = ProgramService.getInstance().getProgramsByOwner(username, LiveStatusEnum.LIVING);

                if (null != programs && programs.size() > 0) {
                    return programs.get(0);
                }
            } catch (LiveHttpException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(final Program program) {
            mGetUserLivingTask = null;

            if (null == program) {
                return;
            }

            if (!mMediaRecorderView.isRecording()) {

                // TODO: 
                DialogManager.alertLivingTerminated(LiveRecordActivity.this, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLivingProgram = program;
                        onClickBtnLiveRecord();
                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = UserManager.getInstance(getApplicationContext()).getUsernamePlain();
                        String coToken = UserManager.getInstance(getApplicationContext()).getToken();

                        LiveControlService.getInstance().updateLiveStatusByCoTokenAsync(coToken, program.getId(), LiveStatusEnum.STOPPED, username);

                        performOnClickStopRecording();
                    }
                }).show();
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
                    LiveAlive liveAlive = LiveControlService.getInstance().keepLiveAlive(coToken, program.getId());

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

    private ChatBox.INewMessageListener newMessageListener = new ChatBox.INewMessageListener() {

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
                String summary = String.format(getString(R.string.share_summary_format), getString(R.string.app_name), title);
                data.putString(ShareDialog.PARAM_TITLE, title);
                data.putString(ShareDialog.PARAM_TARGET_URL, TextUtils.isEmpty(shareUrl) ? getString(R.string.default_share_target_url) : shareUrl);
                data.putString(ShareDialog.PARAM_SUMMARY, summary);
                data.putString(ShareDialog.PARAM_IMAGE_URL, TextUtils.isEmpty(imageUrl) ? getString(R.string.default_share_image_url) : imageUrl);
            } else {
                String title = String.format(getString(R.string.share_user_title), getString(R.string.app_name));
                String shareUrl = getString(R.string.default_share_target_url);
                String imageUrl = getString(R.string.default_share_image_url);
                String summary = String.format(getString(R.string.share_user_format), getString(R.string.app_name));
                data.putString(ShareDialog.PARAM_TITLE, title);
                data.putString(ShareDialog.PARAM_TARGET_URL, shareUrl);
                data.putString(ShareDialog.PARAM_SUMMARY, summary);
                data.putString(ShareDialog.PARAM_IMAGE_URL, imageUrl);
            }
            mShareDialog.setData(data);
        }
    };
}