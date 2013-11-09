package com.pplive.liveplatform.ui;

import java.io.IOException;

import android.content.res.Configuration;
import android.graphics.ColorFilter;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.recorder.CameraManager;
import com.pplive.liveplatform.ui.recorder.LiveMediaRecoder;

public class LiveRecorderActivity extends FragmentActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = LiveRecorderActivity.class.getSimpleName();

    private SurfaceView mPreview;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mCurrentCameraId = CameraManager.CAMERA_FACING_BACK;
    private int mNumberofCameras = CameraManager.getInstance().getNumberOfCameras();
    private boolean mPreviewing = false;
    private boolean mConfigured = false;

    private LiveMediaRecoder mMediaRecorder;
    private boolean mRecording = false;

    private Button mBtnRecord;
    private ToggleButton mBtnSwitchFlashMode;
    
    private RelativeLayout mFooterBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_recorder);

        mPreview = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceHolder = mPreview.getHolder();
        mSurfaceHolder.addCallback(this);

        mBtnRecord = (Button) findViewById(R.id.btn_media_record);
        mBtnSwitchFlashMode = (ToggleButton)findViewById(R.id.btn_switch_flash_mode);
        
        mFooterBar = (RelativeLayout) findViewById(R.id.footer_bar);
        mFooterBar.getBackground().setAlpha(200);
        mFooterBar.getBackground().setColorFilter(new ColorFilter());
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "onConfigurationChanged");
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
        Log.d(TAG, "isFlashOn: " + isFlashOn + "; Status: " + mBtnSwitchFlashMode.getText());
        
        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(isFlashOn? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            
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
        }
    }
    
    private void stopRecording() {
        if (mRecording) {
            mMediaRecorder.stop();

            mRecording = false;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
        case R.id.btn_switch_camera:
            onClickBtnCameraSwitcher(v);
            break;
        case R.id.btn_media_record:
            onClickBtnMediaRecord(v);
            break;
        case R.id.btn_switch_flash_mode:
            onClickBtnSwitchFlashMode(v);
            break;
        default:
            break;
        }
    }

    private void onClickBtnCameraSwitcher(View v) {
        if (mRecording) {
            stopRecording();
        }
        
        stopPreview();

        mCurrentCameraId = (mCurrentCameraId + 1) % mNumberofCameras;
        mCamera = CameraManager.getInstance().open(mCurrentCameraId);

        initCamera();
        startPreview();
    }

    private void onClickBtnMediaRecord(View v) {
        if (null != mCamera) {
            if (!mRecording) {
                startRecording();
            } else {
                stopRecording();
            }
            
            mBtnSwitchFlashMode.setChecked(mRecording);
        }
    }
    
    private void onClickBtnSwitchFlashMode(View v) {
        boolean isFlashOn = mBtnSwitchFlashMode.isChecked();
        
        isFlashOn = setFlashMode(isFlashOn);
        mBtnSwitchFlashMode.setChecked(isFlashOn);
    }

}
