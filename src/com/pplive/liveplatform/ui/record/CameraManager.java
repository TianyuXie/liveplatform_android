package com.pplive.liveplatform.ui.record;

import java.util.List;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.pplive.liveplatform.Constants;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CameraManager {
    
    private static final String TAG = CameraManager.class.getSimpleName();

    public static int DEFAULT_NUMBER_OF_CAMERAS = 1;

    public static int CAMERA_FACING_BACK = CameraInfo.CAMERA_FACING_BACK;

    public static int CAMERA_FACING_FRONT = CameraInfo.CAMERA_FACING_FRONT;

    private static CameraManager sInstance = new CameraManager();

    public static CameraManager getInstance() {
        return sInstance;
    }

    private AsyncTask<Void, Void, Camera> mCameraOpeningTask;

    private CameraManager() {
    };

    public Camera open(final int facing) {
        Camera camera = null;

        if (Constants.LARGER_THAN_OR_EQUAL_GINGERBREAD) {

            int numberOfCameras = Camera.getNumberOfCameras();
            CameraInfo cameraInfo = new CameraInfo();
            for (int cameraId = 0; cameraId < numberOfCameras; ++cameraId) {
                Camera.getCameraInfo(cameraId, cameraInfo);

                if (facing != cameraInfo.facing) {
                    continue;
                }

                camera = Camera.open(cameraId);
            }
        }

        if (null == camera) {
            camera = Camera.open();
        }

        return camera;
    }

    public void openAsync(final int facing, final CameraOpeningListener listener) {
        if (null != mCameraOpeningTask) {
            if (!mCameraOpeningTask.cancel(true)) {
                return;
            }
        }

        mCameraOpeningTask = new AsyncTask<Void, Void, Camera>() {

            @Override
            protected Camera doInBackground(Void... params) {
                Camera camera = open(facing);

                return camera;
            }

            @Override
            protected void onCancelled(Camera camera) {
                if (null != camera) {
                    camera.release();
                }
            }

            @Override
            protected void onPostExecute(Camera camera) {

                if (null != listener) {
                    listener.onOpeningComplete(camera);
                }

                mCameraOpeningTask = null;
            }
        };
        mCameraOpeningTask.execute();
    }

    public int getNumberOfCameras() {
        return Constants.LARGER_THAN_OR_EQUAL_GINGERBREAD ? Camera.getNumberOfCameras() : DEFAULT_NUMBER_OF_CAMERAS;
    }

    public Camera.Size getMiniSize(Parameters params) {
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();

        Camera.Size ms = sizes.get(0);
        for (Camera.Size s : sizes) {
            Log.d(TAG, "s width: " + s.width + "; height: " + s.height);
            
            if (s.width >= 480 && (ms.width < 480 || s.width < ms.width || s.height < ms.height)) {
                ms = s;
            }
        }
        
        Log.d(TAG, "Size width: " + ms.width + "; height: " + ms.height);
        
        return ms;
    }

    interface CameraOpeningListener {

        void onOpeningComplete(Camera camera);
    }
}