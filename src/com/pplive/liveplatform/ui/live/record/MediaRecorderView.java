package com.pplive.liveplatform.ui.live.record;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pplive.liveplatform.ui.live.LiveMediaRecorder;

public class MediaRecorderView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MediaRecorderView.class.getSimpleName();

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    private int mCurrentCameraId = CameraManager.CAMERA_FACING_BACK;
    private int mNumberOfCameras = CameraManager.getInstance().getNumberOfCameras();

    private boolean mPreviewing = false;
    private boolean mConfigured = false;
    private boolean mFlashOn = false;

    private LiveMediaRecorder mMediaRecoder;
    private String mOutputPath;

    private MediaRecorderListener mMediaRecorderListener;

    public MediaRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaRecorderView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        SurfaceHolder holder = this.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");

        mSurfaceHolder = holder;

        startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");

        mSurfaceHolder = null;
    }

    public void setOutputPath(String path) {
        mOutputPath = path;
    }

    public void setMediaRecorderListener(MediaRecorderListener listener) {
        mMediaRecorderListener = listener;
    }

    public boolean isPreviewing() {
        return mPreviewing;
    }

    public void startPreview() {
        Log.d(TAG, "startPreview 1");

        openCamera();
        initCamera();

        if (mConfigured && !mPreviewing && null != mCamera) {
            Log.d(TAG, "startPreview 2");

            mCamera.startPreview();

            mPreviewing = true;
        }
    }

    public void stopPreview() {
        Log.d(TAG, "stopPreview");

        if (null != mCamera) {
            if (isFlashOn()) {
                setFlashMode(false);
            }

            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

            mPreviewing = false;
            mConfigured = false;
        }
    }

    private void openCamera() {
        if (null == mCamera) {
            mCamera = CameraManager.getInstance().open(mCurrentCameraId);
        }
    }

    public void setFlashMode(boolean flashOn) {
        Log.d(TAG, "isFlashOn: " + flashOn);

        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(flashOn ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);

            mCamera.setParameters(params);

            mFlashOn = flashOn;
        }
    }

    public void changeCamera() {
        mCurrentCameraId = (mCurrentCameraId + 1) % mNumberOfCameras;

        changeCamera(mCurrentCameraId);
    }

    public void changeCamera(int cameraId) {
        stopPreview();
        startPreview();

        if (isRecording()) {
            mMediaRecoder.resetCamera(mCamera);
        }
    }

    protected void initCamera() {
        Log.d(TAG, "initCamera 1");

        if (!mConfigured && null != mSurfaceHolder && null != mCamera) {
            Log.d(TAG, "initCamera 2");
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);

                Camera.Parameters params = mCamera.getParameters();

                Camera.Size size = CameraManager.getInstance().getMinSize(params);

                params.setPreviewSize(size.width, size.height);

                params.setPreviewFormat(DevicesChoose.getPreviewImageFormat());
                mCamera.setParameters(params);

                mConfigured = true;
            } catch (IOException e) {
                Log.d(TAG, e.toString());

                mConfigured = false;
            }
        }
    }

    public boolean isFlashOn() {
        return mFlashOn;
    }

    public boolean isRecording() {
        return null == mMediaRecoder ? false : mMediaRecoder.isRecording();
    }

    public void startRecording() {
        Log.d(TAG, "startRecording 1");

        if (TextUtils.isEmpty(mOutputPath)) {
            return;
        }

        if (!isRecording()) {
            Log.d(TAG, "startRecording 2");

            mMediaRecoder = new LiveMediaRecorder(getContext(), mCamera);
            mMediaRecoder.setMediaRecorderListener(mMediaRecorderListener);
            mMediaRecoder.setOutputPath(mOutputPath);

            mMediaRecoder.start();
        }
    }

    public void stopRecording() {
        Log.d(TAG, "stopRecording");

        if (isRecording()) {
            mMediaRecoder.stop();
            mMediaRecoder = null;
        }
    }

    public List<Camera.Size> getSupportedSize() {
        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();

            return params.getSupportedPreviewSizes();
        }

        return null;
    }

    public Camera.Size getPreviewSize() {
        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();

            return params.getPreviewSize();
        }

        return null;
    }
}
