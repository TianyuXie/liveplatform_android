package com.pplive.liveplatform.ui.live;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pplive.liveplatform.ui.live.record.MediaRecorderListener;
import com.pplive.liveplatform.ui.live.record.PPboxSink;
import com.pplive.sdk.MediaSDK;
import com.pplive.sdk.MediaSDK.Download_Callback;
import com.pplive.sdk.MediaSDK.Upload_Statistic;

public class LiveMediaRecorder implements Handler.Callback {

    private static final String TAG = LiveMediaRecorder.class.getSimpleName();

    private static final int WHAT_CHECK_UPLOAD_INFO = 9001;

    private static final int WHAT_CHECK_REMAINING_TIME = 9002;
    
    private static final int WHAT_ERROR = 9009;

    private static final int DELAY_CHECK_UPLOAD_INFO = 500; // millisecond
    
    private static final int DELAY_CHECK_REMAINING_TIME = 5000; // millisecond

    private PPboxSink mCapture;

    private String mOutputPath;

    private boolean mRecording = false;
    
    private Quality mQuality = Quality.Normal;

    private Handler mInnerHandler = new Handler(this);

    private MediaRecorderListener mMediaRecorderListener;

    private Upload_Statistic mUploadStatistic = new Upload_Statistic();

    private Download_Callback mDownloaCallback = new Download_Callback() {

        @Override
        public void invoke(long err) {
            Log.d(TAG, "error: " + err);

            if (mRecording) {
                mInnerHandler.sendEmptyMessage(WHAT_ERROR);
            }
        }
    };

    public LiveMediaRecorder(Context ctx, Camera camera) {

        PPboxSink.init(ctx.getApplicationContext());
        mCapture = new PPboxSink(camera);
    }

    public void setOutputPath(String url) {
        mOutputPath = url;
        //        mOutputPath = "rtmp://172.16.205.53:1936/push/test?ts=1386312842&token=44b3f8302518eb86b1f16b3cb3c05f63";
        //        mOutputPath = "/sdcard/test.flv";
    }

    public void setMediaRecorderListener(MediaRecorderListener listener) {
        mMediaRecorderListener = listener;

        mCapture.setDownloadCallback(mDownloaCallback);
    }

    public void start() {
        if (null != mCapture) {
            mRecording = true;

            mCapture.open(mOutputPath);
            mCapture.start();

            mInnerHandler.sendEmptyMessageDelayed(WHAT_CHECK_UPLOAD_INFO, DELAY_CHECK_UPLOAD_INFO);
        }
    }

    public void stop() {
        if (null != mCapture) {
            mRecording = false;

            mCapture.stop();
            mCapture.close();
            mCapture = null;
        }
    }

    public boolean isRecording() {
        return mRecording;
    }

    public void resetCamera(Camera camera) {
        if (null != mCapture) {
            mCapture.resetCamera(camera);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case WHAT_CHECK_UPLOAD_INFO:
            onCheckUploadInfo();
            break;
        case WHAT_CHECK_REMAINING_TIME:
            onCheckRemainingTime();
            break;
        case WHAT_ERROR:
            onError();
            break;
        default:
            break;
        }

        return false;
    }

    private void onCheckUploadInfo() {

        Log.d(TAG, "onCheckUploadInfo");

        if (null != mCapture) {
            mUploadStatistic.time = 0;
            long ret = MediaSDK.CaptureStatInfo(mCapture.getCaptureId(), mUploadStatistic);

            Log.d(TAG, "ret: " + ret);

            if (0 == ret) {
                if (mUploadStatistic.time > 0) {

                    Log.d(TAG, "mUploadStatistic.time: " + mUploadStatistic.time);

                    onSuccess();
                    
                    mInnerHandler.sendEmptyMessageDelayed(WHAT_CHECK_REMAINING_TIME, DELAY_CHECK_REMAINING_TIME);
                } else {
                    mInnerHandler.sendEmptyMessageDelayed(WHAT_CHECK_UPLOAD_INFO, DELAY_CHECK_UPLOAD_INFO);
                }
            }
        }
    }
    
    private void onCheckRemainingTime() {
        Log.d(TAG, "onCheckRemainingtime");
        if (null != mCapture) {
            mUploadStatistic.remaining_time = 0;
            long ret = MediaSDK.CaptureStatInfo(mCapture.getCaptureId(), mUploadStatistic);
            
            Log.d(TAG, "ret: " + ret);
            if (0 == ret) {
                int remaining_time = mUploadStatistic.remaining_time;
                Log.d(TAG, "mUploadStatistic.remaining_time: " + remaining_time);
                
                if (remaining_time > 0 && remaining_time < 500 /* millisecond */) {
                    
                    mQuality = mQuality.next();
                    
                } else if (remaining_time > 2500 /* millisecond */) {
                    
                    mQuality = mQuality.previous();
                }
                
                Log.d(TAG, "interval: " + mQuality.interval());
                
                mCapture.setPreviewInterval(mQuality.interval());
            }
            
            if (mRecording) {
                mInnerHandler.sendEmptyMessageDelayed(WHAT_CHECK_REMAINING_TIME, DELAY_CHECK_REMAINING_TIME);
            }
        }
    }

    private void onSuccess() {
        if (null != mMediaRecorderListener) {
            mMediaRecorderListener.onSuccess();
        }
    }

    private void onError() {

        stop();

        if (null != mMediaRecorderListener) {
            mMediaRecorderListener.onError();
        }
    }
    
    enum Quality {
        High {
          
            @Override
            Quality next() {
                return High;
            }
            
            @Override
            Quality previous() {
                return Normal;
            }
            
            @Override
            int interval() {
                return 25;
            }
        },
        Normal {
            
            @Override
            Quality next() {
                return High;
            }
            
            @Override
            Quality previous() {
                return Low;
            }
            
            @Override
            int interval() {
                return 50;
            }
        },
        
        Low {
            
            @Override
            Quality next() {
                return Normal;
            }
            
            @Override
            Quality previous() {
                return Low;
            }
            
            @Override
            int interval() {
                return 80;
            }
        };
        
        abstract Quality next();
        
        abstract Quality previous();
        
        abstract int interval();
    }
}
