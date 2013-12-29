package com.pplive.liveplatform.ui.record;

import java.io.File;
import java.nio.ByteBuffer;

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

    private PPboxStream mVideoStream;

    private PPboxStream mAudioStream;

    private Thread mAudioThread;

    private long mStartTime;

    private Download_Callback mDownloadCallback;

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        private final long time_scale = 1000 * 1000 * 1000;
        private int num_total = 0;
        private int num_drop = 0;
        private long next_time = 5 * time_scale;

        private long put_preview_interval = 50 /* millisecond */;

        private long last_put_preview_time = System.currentTimeMillis();

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            ++num_total;

            long cur_time = System.currentTimeMillis();
            long time_stamp = System.nanoTime() - mStartTime;

            Log.d(TAG, "interval: " + (cur_time - last_put_preview_time));

            if (cur_time - last_put_preview_time > put_preview_interval) {
                PPboxStream.InBuffer buffer = mVideoStream.pop();
                if (buffer == null) {
                    ++num_drop;
                } else {
                    Log.d(TAG, "image size: " + data.length);

                    buffer.byte_buffer().put(data);
                    mVideoStream.put(time_stamp / 1000, buffer);
                    last_put_preview_time = cur_time;
                }

                if (time_stamp >= next_time) {
                    Log.d(TAG, "video " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: " + num_drop);
                    next_time += 5 * time_scale;
                }
            }

            camera.addCallbackBuffer(data);
        }
    };

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
        mVideoStream = new PPboxStream(mCaptureId, 0, mCamera);
        mAudioStream = new PPboxStream(mCaptureId, 1, mAudioRecord);
    }

    public void changeCamera(Camera camera) {
        mCamera = camera;

        if (null != mVideoStream) {
            final byte[] video_buffer = new byte[mVideoStream.bufferSize()];

            camera.addCallbackBuffer(video_buffer);
            camera.setPreviewCallbackWithBuffer(mPreviewCallback);
        }
    }

    public void start() {
        mVideoStream.start();

        changeCamera(mCamera);

        mAudioStream.start();
        mAudioThread = new Thread() {
            @Override
            public void run() {
                audio_read_thread();
            }
        };
        mAudioThread.setPriority(Thread.MAX_PRIORITY);
        mAudioThread.start();
    }

    private void audio_read_thread() {
        final long time_scale = 1000 * 1000 * 1000;
        final int read_size = mAudioStream.bufferSize();
        int num_total = 0;
        int num_drop = 0;
        long next_time = 5 * time_scale;

        ByteBuffer drop_buffer = ByteBuffer.allocateDirect(read_size);

        mAudioRecord.startRecording();
        while (!Thread.interrupted()) {
            long time = System.nanoTime() - mStartTime;
            if (time >= next_time) {
                Log.d(TAG, "audio " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: " + num_drop);
                next_time += 5 * time_scale;
            }
            ++num_total;
            PPboxStream.InBuffer buffer = mAudioStream.pop();
            if (buffer == null) {
                // System.out.println("audio drop");
                mAudioRecord.read(drop_buffer, read_size);
                mAudioStream.drop();
                ++num_drop;
                continue;
            }
            int read = mAudioRecord.read(buffer.byte_buffer(), read_size);
            if (read != read_size) {
                Log.d(TAG, "audio.read failed. read = " + read);
                break;
            }

            mAudioStream.put(time / 1000, buffer);
        }

        mAudioRecord.stop();
    }

    public void stop() {
        mAudioThread.interrupt();
        try {
            mAudioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mCamera.setPreviewCallbackWithBuffer(null);
    }

    public void close() {

        if (null != mAudioRecord) {
            mAudioRecord.release();
            mAudioRecord = null;
        }

        if (null != mVideoStream) {
            mVideoStream.stop();
            mVideoStream = null;
        }

        if (null != mAudioStream) {
            mAudioStream.stop();
            mAudioStream = null;
        }

        Log.d(TAG, "Before destroy capture");
        MediaSDK.CaptureDestroy(mCaptureId);
        Log.d(TAG, "After destroy capture");
    }

}
