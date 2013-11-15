package com.pplive.liveplatform.ui;

import java.io.IOException;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.record.CameraManager;
import com.pplive.liveplatform.ui.record.LiveMediaRecoder;
import com.pplive.liveplatform.ui.widget.FooterBar;
import com.pplive.liveplatform.ui.widget.HorizontalListView;
import com.pplive.liveplatform.util.TimeUtil;

public class LiveRecordActivity extends FragmentActivity implements View.OnClickListener, SurfaceHolder.Callback, Handler.Callback {

    private static final String TAG = LiveRecordActivity.class.getSimpleName();

    private static final int WHAT_RECORD_START = 9001;
    private static final int WHAT_RECORD_END = 9002;
    private static final int WHAT_RECORD_UPDATE = 9003;

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

    private FooterBar mFooterBar;

    private HorizontalListView mLiveListView;

    private TextView mTextLive;
    private TextView mTextRecordDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_record);

        mPreview = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceHolder = mPreview.getHolder();
        mSurfaceHolder.addCallback(this);

        mBtnLiveRecord = (ToggleButton) findViewById(R.id.btn_live_record);
        mBtnFlashLight = (ToggleButton) findViewById(R.id.btn_flash_light);

        mFooterBar = (FooterBar) findViewById(R.id.footer_bar);
        mLiveListView = mFooterBar.getLiveListView();

        mTextLive = (TextView) findViewById(R.id.text_live);
        mTextRecordDuration = (TextView) findViewById(R.id.text_record_duration);
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
        default:
            break;
        }

        return false;
    }

    private void onRecordStart() {
        mTextLive.setVisibility(View.VISIBLE);
        mTextRecordDuration.setVisibility(View.VISIBLE);

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
        if (!mRecording) {
            mMediaRecorder = new LiveMediaRecoder(getApplicationContext(), mCamera);

            mMediaRecorder.start();

            mRecording = true;

            mInnerHandler.sendEmptyMessage(WHAT_RECORD_START);
        }
    }

    private void stopRecording() {
        if (mRecording) {
            mMediaRecorder.stop();

            mRecording = false;

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
                startRecording();
            } else {
                stopRecording();
            }

            mBtnLiveRecord.setChecked(mRecording);
        }
    }

    private void onClickBtnFlashLight(View v) {
        boolean isFlashOn = mBtnFlashLight.isChecked();

        isFlashOn = setFlashMode(isFlashOn);
        mBtnFlashLight.setChecked(isFlashOn);
    }

}
