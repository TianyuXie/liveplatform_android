package com.pplive.liveplatform.ui.recorder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import android.view.SurfaceHolder;

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

    public void open(String url) {
        mCaptureId = MediaSDK.CaptureOpen("pprecord://record", "rtmp", url, new Download_Callback() {

            @Override
            public void invoke(long err) {
                Log.d(TAG, "err:  " + err);
            }
        });

        // TODO: DEBUG
        mAudioRecord = getAudioRecord();

        MediaSDK.CaptureConfigData config = new MediaSDK.CaptureConfigData();

        // TODO: Debug
        config.stream_count = 2;
        config.thread_count = 2; // multi_thread
        config.sort_type = 1;

        MediaSDK.CaptureInit(mCaptureId, config);

        mStartTime = System.nanoTime();
        mVideoStream = new PPboxStream(mCaptureId, 0, mCamera);
        mAudioStream = new PPboxStream(mCaptureId, 1, mAudioRecord);
    }

    public void preview(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {

        } catch (Exception e) {

        }
    }

    public void start() {
        mVideoStream.start();

        final byte[] video_buffer = new byte[mVideoStream.bufferSize()];

        mCamera.addCallbackBuffer(video_buffer);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            private final long time_scale = 1000 * 1000 * 1000;
            private int num_total = 0;
            private int num_drop = 0;
            private long next_time = 5 * time_scale;

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                ++num_total;
                long time = System.nanoTime() - mStartTime;
                PPboxStream.InBuffer buffer = mVideoStream.pop();
                if (buffer == null) {
                    ++num_drop;
                    // System.out.println("video drop");
                } else {
                    buffer.byte_buffer().put(data);
                    mVideoStream.put(time / 1000, buffer);
                }
                if (time >= next_time) {
                    System.out.println("video " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: "
                            + num_drop);
                    next_time += 5 * time_scale;
                }
                camera.addCallbackBuffer(data);
            }
        });

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
        //        camera.release();

        // TODO: DEBUG
        if (null != mAudioRecord) {
            mAudioRecord.release();
            mAudioRecord = null;
        }

        Log.d(TAG, "Before destroy capture");
        MediaSDK.CaptureDestroy(mCaptureId);
        Log.d(TAG, "After destroy capture");

        mVideoStream.stop();
        mAudioStream.stop();

        mVideoStream = null;
        mAudioStream = null;
    }

    private static int[] sampleRates = new int[] { 8000, 11025, 22050, 44100 };
    private static short[] channelConfigs = new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO };
    private static short[] audioFormats = new short[] { AudioFormat.ENCODING_PCM_16BIT };

    private AudioRecord getAudioRecord() {
        for (int sampleRate : sampleRates) {
            for (short channelConfig : channelConfigs) {
                for (short audioFormat : audioFormats) {
                    try {
                        Log.d(TAG, "Attempting rate " + sampleRate + "Hz, channel: " + channelConfig + ", bits: " + audioFormat);
                        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                        if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }

                        Log.d(TAG, "bufferSize: " + bufferSize);

                        bufferSize = PPboxStream.frame_size(1024, channelConfig, audioFormat) * 16;
                        // check if we can instantiate and have a success
                        AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, bufferSize);

                        Log.d(TAG, "state: " + recorder.getState());

                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                            Log.d(TAG, "Supported. buffer size: " + bufferSize);
                            return recorder;
                        }

                        Log.d(TAG, "release audio recorder");
                        recorder.release();
                    } catch (Exception e) {
                        Log.d(TAG, "Exception, keep trying." + e);
                    }
                }
            }
        }
        return null;
    }
}
