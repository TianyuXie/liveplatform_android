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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.LiveStatusEnum;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.model.Push;
import com.pplive.liveplatform.core.rest.service.LiveControlService;
import com.pplive.liveplatform.core.rest.service.MediaService;
import com.pplive.liveplatform.core.rest.service.ProgramService;
import com.pplive.liveplatform.core.rest.service.TokenService;
import com.pplive.liveplatform.ui.record.CameraManager;
import com.pplive.liveplatform.ui.record.FooterBarFragment;
import com.pplive.liveplatform.ui.record.LiveMediaRecoder;
import com.pplive.liveplatform.ui.record.LiveMediaRecoder.OnErrorListener;
import com.pplive.liveplatform.ui.record.OnLiveSelectedListener;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.util.TimeUtil;

public class LiveRecordActivity extends FragmentActivity implements View.OnClickListener, SurfaceHolder.Callback, Handler.Callback {

    private static final String TAG = LiveRecordActivity.class.getSimpleName();

    private static final int WHAT_RECORD_START = 9001;
    private static final int WHAT_RECORD_END = 9002;
    private static final int WHAT_RECORD_UPDATE = 9003;

    private static final int WHAT_LIVE_COMING_START = 9004;
    private static final int WHAT_LIVE_COMING_UPDATE = 9005;

    private static final int WHAT_OPEN_DOOR = 9006;

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

    private ToggleButton mBtnLiveRecord;
    private ToggleButton mBtnFlashLight;

    private FooterBarFragment mFooterBarFragment;

    private TextView mTextLive;
    private TextView mTextRecordDuration;
    private TextView mTextLiveComing;
    private TextView mTextLivingTitle;

    private AnimDoor mAnimDoor;
    private View mStatusButtonWrapper;
    private Animation mStatusUpAnimation;
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

    private Program mSelectedProgram;
    private OnLiveSelectedListener mOnLiveSelectedListener = new OnLiveSelectedListener() {

        @Override
        public void onLiveSelected(Program program) {
            mSelectedProgram = program;

            mInnerHandler.sendEmptyMessage(WHAT_LIVE_COMING_START);
        }
    };

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

        mBtnLiveRecord = (ToggleButton) findViewById(R.id.btn_live_record);
        mBtnFlashLight = (ToggleButton) findViewById(R.id.btn_flash_light);

        mFooterBarFragment = (FooterBarFragment) getSupportFragmentManager().findFragmentById(R.id.footer_bar);
        mFooterBarFragment.setOnLiveSelected(mOnLiveSelectedListener);

        mTextLive = (TextView) findViewById(R.id.text_live);
        mTextRecordDuration = (TextView) findViewById(R.id.text_record_duration);
        mTextLiveComing = (TextView) findViewById(R.id.text_live_coming);
        mTextLivingTitle = (TextView) findViewById(R.id.text_living_title);

        mAnimDoor = (AnimDoor) findViewById(R.id.live_animdoor);
        mAnimDoor.setOpenDoorListener(openDoorListener);

        mStatusButtonWrapper = findViewById(R.id.wrapper_live_status);
        mStatusButton = (LoadingButton) findViewById(R.id.btn_live_status);

        mStatusUpAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -mAnimDoor.getAnimX() * 1.3f);
        mStatusUpAnimation.setFillAfter(true);
        mStatusUpAnimation.setDuration((int) (mAnimDoor.getDuration() * 1.3f));
        mStatusUpAnimation.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCamera = CameraManager.getInstance().open(mCurrentCameraId);

        startPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRecording) {
            stopRecording();
        }

        stopPreview();
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
        case WHAT_OPEN_DOOR:
            mStatusButton.finishLoading();
            mStatusButtonWrapper.startAnimation(mStatusUpAnimation);
            mAnimDoor.open();
            break;
        default:
            break;
        }

        return false;
    }

    private void onRecordStart() {
        mTextLive.setVisibility(View.VISIBLE);
        mTextRecordDuration.setVisibility(View.VISIBLE);
        
        mTextLivingTitle.setVisibility(View.VISIBLE);
        mTextLivingTitle.setText(mSelectedProgram.getTitle());

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

        if (null != mSelectedProgram) {
            long now = System.currentTimeMillis();
            long start = mSelectedProgram.getStartTime();

            Log.d(TAG, "now: " + now + "; start: " + start);

            String coming = null;
            if (start - now > 0) {
                coming = TimeUtil.stringForTime(start - now);
            } else {
                coming = TimeUtil.stringForTime(0);
            }

            mTextLiveComing.setText(coming);
            mInnerHandler.sendEmptyMessageDelayed(WHAT_LIVE_COMING_UPDATE, 1000 /* milliseconds */);
        }
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

    private void startRecording(String url) {
        if (!mRecording) {
            mMediaRecorder = new LiveMediaRecoder(getApplicationContext(), mCamera);
            mMediaRecorder.setOnErrorListener(new OnErrorListener() {

                @Override
                public void onError() {
                    //                    stopRecording();
                }
            });

            mMediaRecorder.setOutputPath(url);

            mMediaRecorder.start();

            mRecording = true;

            mInnerHandler.sendEmptyMessage(WHAT_RECORD_START);
        }
    }

    private void stopRecording() {
        if (mRecording) {
            mMediaRecorder.stop();

            mRecording = false;

            mBtnLiveRecord.setChecked(mRecording);

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
                    mGetPushUrlOneStepTask.execute(mSelectedProgram);
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

    private GetPushUrlTask mGetPushUrlOneStepTask;

    class GetPushUrlTask extends AsyncTask<Program, Void, String> {

        @Override
        protected String doInBackground(Program... params) {

            Program program = params[0];
            if (null == program) {
                Log.d(TAG, "create program");
                program = new Program("xiety0001", "My Living", System.currentTimeMillis());
                program = ProgramService.getInstance().createProgram(program);
                mSelectedProgram = program;
            } else {
                Log.d(TAG, "has program");
            }
            
            String token = TokenService.getInstance().getLiveToken(program.getId(), program.getOwner());
            
            LiveControlService.getInstance().updateLiveStatusWithToken(program.getId(), LiveStatusEnum.INIT, token);
            LiveControlService.getInstance().updateLiveStatusWithToken(program.getId(), LiveStatusEnum.PREVIEW, token);
            LiveControlService.getInstance().updateLiveStatusWithToken(program.getId(), LiveStatusEnum.LIVING, token);

            Push push = MediaService.getInstance().getPush(program.getId(), "xiety0001");

            String url = null;
            for (int i = 0, len = push.getPushStringList().size(); i < len; ++i) {
                url = push.getPushStringList().get(i);
                if (!TextUtils.isEmpty(url)) {
                    break;
                }
            }

            return url;
        }

        @Override
        protected void onPostExecute(String result) {
            mGetPushUrlOneStepTask = null;

            if (null != mCamera) {
                if (!mRecording) {
                    startRecording(result);
                }

                mBtnLiveRecord.setChecked(mRecording);
            }
        }
    }
}