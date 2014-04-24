package com.pplive.liveplatform.core.record;

import java.io.File;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.util.Log;

import com.pplive.sdk.MediaSDK;
import com.pplive.sdk.MediaSDK.Download_Callback;

public class PPboxSink {

    private static final String TAG = PPboxSink.class.getSimpleName();

    private long mCaptureId;

    private Camera mCamera;

    private AudioRecord mAudioRecord;

    private PPboxVideoStream mVideoStream;

    private PPboxAudioStream mAudioStream;

    private long mStartTime;

    private Download_Callback mDownloadCallback;

    public static void init(Context c) {
        File cacheDirFile = c.getCacheDir();
        String dataDir = cacheDirFile.getParentFile().getAbsolutePath();
        String libDir = dataDir + "/lib";
        String tmpDir = System.getProperty("java.io.tmpdir") + "/ppsdk";
        File tmpDirFile = new File(tmpDir);
        tmpDirFile.mkdir();

        MediaSDK.libPath = libDir;
        MediaSDK.logPath = tmpDir;
        MediaSDK.logLevel = MediaSDK.LEVEL_EVENT;
        MediaSDK.startP2PEngine("161", "12", "111");
    }

    public PPboxSink(Camera camera) {
        this.mCamera = camera;
    }
    
    public long getCaptureId() {
        return mCaptureId;
    }

    public void setDownloadCallback(Download_Callback callback) {
        mDownloadCallback = callback;
    }
    
    public void open(String url) {
        Log.d(TAG, "url: " + url);

        mCaptureId = MediaSDK.CaptureOpen("pprecord://record", "rtmp", url, mDownloadCallback);

        mAudioRecord = MediaManager.getInstance().getAudioRecord();

        MediaSDK.CaptureConfigData config = new MediaSDK.CaptureConfigData();

        config.stream_count = 2;
        config.thread_count = 2; // multi_thread
        config.sort_type = 1;

        MediaSDK.CaptureInit(mCaptureId, config);

        mStartTime = System.nanoTime();
        mVideoStream = new PPboxVideoStream(mCaptureId, 0, mStartTime, mCamera);
        mAudioStream = new PPboxAudioStream(mCaptureId, 1, mStartTime, mAudioRecord);
    }

    public void resetCamera(Camera camera) {
        mCamera = camera;
        
        if (null != mVideoStream) {
            mVideoStream.resetCamera(mCamera);
        }
    }

    public void start() {
        mVideoStream.start();
        mAudioStream.start();
    }

    public void stop() {
        mVideoStream.stop();
        mAudioStream.stop();
    }

    public void close() {

        if (null != mVideoStream) {
            mVideoStream.close();
            mVideoStream = null;
        }

        if (null != mAudioStream) {
            mAudioStream.close();
            mAudioStream = null;
        }
        
        if (null != mAudioRecord) {
            mAudioRecord.release();
            mAudioRecord = null;
        }

        Log.d(TAG, "Before destroy capture");
        MediaSDK.CaptureDestroy(mCaptureId);
        Log.d(TAG, "After destroy capture");
    }

    public void setPreviewInterval(int interval) {
        if (null != mVideoStream) {
            mVideoStream.setPreviewInterval(interval);
        }
    }
}
