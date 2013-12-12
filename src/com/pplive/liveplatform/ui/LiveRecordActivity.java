package com.pplive.liveplatform.ui;

import java.io.IOException;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import com.pplive.liveplatform.core.service.live.LiveControlService;
import com.pplive.liveplatform.core.service.live.MediaService;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.TokenService;
import com.pplive.liveplatform.core.service.live.model.LiveAlive;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.Push;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.record.CameraManager;
import com.pplive.liveplatform.ui.record.FooterBarFragment;
import com.pplive.liveplatform.ui.record.LiveMediaRecoder;
import com.pplive.liveplatform.ui.record.LiveMediaRecoder.OnErrorListener;
import com.pplive.liveplatform.ui.record.event.EventProgramSelected;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeUtil;

import de.greenrobot.event.EventBus;

public class LiveRecordActivity extends FragmentActivity implements View.OnClickListener, SurfaceHolder.Callback, Handler.Callback {

    private static final String TAG = LiveRecordActivity.class.getSimpleName();

    private static final int WHAT_RECORD_START = 9001;
    private static final int WHAT_RECORD_END = 9002;
    private static final int WHAT_RECORD_UPDATE = 9003;

    private static final int WHAT_LIVE_COMING_START = 9004;
    private static final int WHAT_LIVE_COMING_UPDATE = 9005;

    private static final int WHAT_LIVE_KEEP_ALIVE = 9006;

    private static final int WHAT_OPEN_DOOR = 9010;

    private Handler mInnerHandler = new Handler(this);

    private SurfaceView mPreview;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mCurrentCameraId = CameraManager.CAMERA_FACING_BACK;
    private int mNumberofCameras = CameraManager.getInstance().getNumberOfCameras();
    private boolean mPreviewing = false;
    private boolean mConfigured = false;

    private LiveMediaRecoder mMediaRecorder;
    private boolean mRecording = false;

    private ImageButton mBtnLiveRecord;
    private ImageButton mBtnCameraChange;
    private ToggleButton mBtnFlashLight;

    private FooterBarFragment mFooterBarFragment;

    private TextView mTextLive;
    private TextView mTextRecordDuration;
    private TextView mTextLiveComing;
    private TextView mTextLivingTitle;

    private AnimDoor mAnimDoor;
    private View mStatusButtonWrapper;
    private View mLiveButtonWrapper;

    private LoadingButton mStatusButton;

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
    private KeepLiveAliveTask mKeepLiveAliveTask;
    private Program mLivingProgram;

    private String mPushUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_record);

        mPreview = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceHolder = mPreview.getHolder();
        mSurfaceHolder.addCallback(this);

        mBtnLiveRecord = (ImageButton) findViewById(R.id.btn_live_record);
        mBtnCameraChange = (ImageButton) findViewById(R.id.btn_camera_change);
        mBtnFlashLight = (ToggleButton) findViewById(R.id.btn_flash_light);

        mFooterBarFragment = (FooterBarFragment) getSupportFragmentManager().findFragmentById(R.id.footer_bar);

        mTextLive = (TextView) findViewById(R.id.text_live);
        mTextRecordDuration = (TextView) findViewById(R.id.text_record_duration);
        mTextLiveComing = (TextView) findViewById(R.id.text_live_coming);
        mTextLivingTitle = (TextView) findViewById(R.id.text_living_title);

        mAnimDoor = (AnimDoor) findViewById(R.id.live_animdoor);
        mAnimDoor.setOpenDoorListener(openDoorListener);

        mStatusButtonWrapper = findViewById(R.id.wrapper_live_status);
        mLiveButtonWrapper = findViewById(R.id.wrapper_live_status_right);
        mStatusButton = (LoadingButton) findViewById(R.id.btn_live_status);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        mCamera = CameraManager.getInstance().open(mCurrentCameraId);

        startPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopRecording();

        stopPreview();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Log.d(TAG, "open");
            mStatusButton.startLoading();
            mInnerHandler.sendEmptyMessageDelayed(WHAT_OPEN_DOOR, 2000);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "onConfigurationChanged");
    }

    public void onEvent(EventProgramSelected event) {
        final Program program = event.getObject();

        mLivingProgram = program;

        mInnerHandler.sendEmptyMessage(WHAT_LIVE_COMING_START);
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
        case WHAT_LIVE_COMING_START:
            onLiveComingStart();
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
        rotation.setRotateListener(rotateListener);
        mLiveButtonWrapper.startAnimation(rotation);
    }

    private RotateListener rotateListener = new RotateListener() {
        @Override
        public void onRotateMiddle() {
            mBtnLiveRecord.setBackgroundResource(R.drawable.live_record_btn_live_record);
        }
    };

    private void onRecordStart() {
        mTextLive.setVisibility(View.VISIBLE);
        mTextRecordDuration.setVisibility(View.VISIBLE);

        mTextLivingTitle.setVisibility(View.VISIBLE);
        mTextLivingTitle.setText(mLivingProgram.getTitle());

        Message msg = mInnerHandler.obtainMessage(WHAT_RECORD_UPDATE);
        mInnerHandler.sendMessage(msg);
    }

    private void onRecordEnd() {
        mTextLive.setVisibility(View.GONE);
        mTextRecordDuration.setVisibility(View.GONE);
    }

    private void onRecordUpdate(int duration) {

        if (mRecording) {
            mTextRecordDuration.setText(TimeUtil.stringForTime(duration * 1000));

            Message msg = mInnerHandler.obtainMessage(WHAT_RECORD_UPDATE, duration + 1 /* arg1 */, 0 /* arg2 */);
            mInnerHandler.sendMessageDelayed(msg, 1000 /* milliseconds */);
        }
    }

    private void onLiveComingStart() {
        mTextLiveComing.setVisibility(View.VISIBLE);

        mInnerHandler.sendEmptyMessage(WHAT_LIVE_COMING_UPDATE);
    }

    private void onLiveComingUpdate() {
        if (mRecording) {
            return;
        }

        if (null != mLivingProgram) {
            long now = System.currentTimeMillis();
            long start = mLivingProgram.getStartTime();

            Log.d(TAG, "now: " + now + "; start: " + start);

            String coming = null;
            if (start - now > 0) {
                coming = TimeUtil.stringForTime(start - now);
            } else {
                coming = TimeUtil.stringForTime(0);
            }

            mTextLiveComing.setText(coming);

            if (!isFinishing()) {
                mInnerHandler.sendEmptyMessageDelayed(WHAT_LIVE_COMING_UPDATE, 1000 /* milliseconds */);
            }

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;

        initCamera();
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void initCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);

                Parameters params = mCamera.getParameters();

                Camera.Size size = CameraManager.getInstance().getMiniSize(params);

                params.setPreviewSize(size.width, size.height);
                params.setPreviewFormat(ImageFormat.NV21);

                mCamera.setParameters(params);
                mConfigured = true;
            } catch (IOException e) {
                Log.w(TAG, "Init camera failed. ", e);
            }
        }
    }

    private boolean setFlashMode(boolean isFlashOn) {
        Log.d(TAG, "isFlashOn: " + isFlashOn + "; Status: " + mBtnFlashLight.getText());

        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(isFlashOn ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);

            mCamera.setParameters(params);
        }

        return isFlashOn;
    }

    private void startPreview() {
        if (mConfigured && !mPreviewing && null != mCamera) {
            mCamera.startPreview();
            mPreviewing = true;
        }
    }

    private void stopPreview() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

            mPreviewing = false;
        }
    }

    private void startRecording() {
        if (TextUtils.isEmpty(mPushUrl)) {
            return;
        }

        if (!mRecording) {
            mMediaRecorder = new LiveMediaRecoder(getApplicationContext(), mCamera);
            mMediaRecorder.setOnErrorListener(new OnErrorListener() {

                @Override
                public void onError() {
                    //                    stopRecording();
                }
            });

            mMediaRecorder.setOutputPath(mPushUrl);

            mMediaRecorder.start();

            mRecording = true;

            mInnerHandler.sendEmptyMessage(WHAT_RECORD_START);

            mInnerHandler.sendEmptyMessage(WHAT_LIVE_KEEP_ALIVE);
        }
    }

    private void stopRecording() {
        if (mRecording) {
            mMediaRecorder.stop();
            mRecording = false;
            mBtnLiveRecord.setSelected(mRecording);
            mInnerHandler.sendEmptyMessage(WHAT_RECORD_END);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
        case R.id.btn_camera_change:
            onClickBtnCameraChange(v);
            break;
        case R.id.btn_live_record:
            onClickBtnLiveRecord(v);
            break;
        case R.id.btn_flash_light:
            onClickBtnFlashLight(v);
            break;
        default:
            break;
        }
    }

    private void onClickBtnCameraChange(View v) {
        if (mRecording) {
            stopRecording();
        }

        stopPreview();

        mCurrentCameraId = (mCurrentCameraId + 1) % mNumberofCameras;
        mCamera = CameraManager.getInstance().open(mCurrentCameraId);

        initCamera();
        startPreview();
    }

    private void onClickBtnLiveRecord(View v) {

        if (null != mCamera) {
            if (!mRecording) {
                getSupportFragmentManager().beginTransaction().hide(mFooterBarFragment).commit();
                mTextLiveComing.setVisibility(View.GONE);
                if (null == mGetPushUrlOneStepTask) {
                    mGetPushUrlOneStepTask = new GetPushUrlTask();
                    mGetPushUrlOneStepTask.execute(mLivingProgram);
                }
            } else {
                stopRecording();
            }
        }
    }

    private void onClickBtnFlashLight(View v) {
        boolean isFlashOn = mBtnFlashLight.isChecked();

        isFlashOn = setFlashMode(isFlashOn);
        mBtnFlashLight.setChecked(isFlashOn);

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
            String username = UserManager.getInstance(getApplicationContext()).getActiveUserPlain();
            String usertoken = UserManager.getInstance(getApplicationContext()).getToken();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(usertoken)) {
                return null;
            }

            Program program = params[0];
            if (null == program) {
                Log.d(TAG, "create program");
                program = new Program(username, mLiveTitle, System.currentTimeMillis());
                program = ProgramService.getInstance().createProgram(usertoken, program);
                mLivingProgram = program;
            } else {
                Log.d(TAG, "has program");
            }

            String liveToken = program.getLiveToken();
            if (TextUtils.isEmpty(liveToken)) {
                Log.d(TAG, "getLiveToken");
                liveToken = TokenService.getInstance().getLiveToken(usertoken, program.getId(), username);
            }

            LiveControlService.getInstance().updateLiveStatusByLiveToken(liveToken, program.getId(), LiveStatusEnum.INIT);
            LiveControlService.getInstance().updateLiveStatusByLiveToken(liveToken, program.getId(), LiveStatusEnum.PREVIEW);
            LiveControlService.getInstance().updateLiveStatusByLiveToken(liveToken, program.getId(), LiveStatusEnum.LIVING);

            Push push = MediaService.getInstance().getPushByLiveToken(program.getId(), liveToken);

            String url = null;
            for (int i = 0, len = push.getPushUrlList().size(); i < len; ++i) {
                url = push.getPushUrlList().get(i);
                if (!TextUtils.isEmpty(url)) {
                    break;
                }
            }

            return url;
        }

        @Override
        protected void onPostExecute(String url) {
            mGetPushUrlOneStepTask = null;

            if (null != mCamera) {
                if (!mRecording) {
                    mPushUrl = url;
                    startRecording();
                }

                mBtnLiveRecord.setSelected(mRecording);
            }
        }
    }

    class KeepLiveAliveTask extends AsyncTask<Program, Void, LiveAlive> {

        @Override
        protected LiveAlive doInBackground(Program... params) {
            String coToken = UserManager.getInstance(getApplicationContext()).getToken();

            Program program = params[0];

            if (null != program && !TextUtils.isEmpty(coToken)) {
                LiveAlive liveAlive = LiveControlService.getInstance().keepLiveAlive(coToken, program.getId());

                return liveAlive;
            }

            return null;
        }

        @Override
        protected void onPostExecute(LiveAlive result) {
            mKeepLiveAliveTask = null;

            long delay = 60; // millisecond
            if (null != result) {
                delay = result.getDelayInMillis();
            }

            if (mRecording) {
                mInnerHandler.sendEmptyMessageDelayed(WHAT_LIVE_KEEP_ALIVE, delay);
            }
        }
    }
}