package com.pplive.liveplatform.ui.record;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.pplive.sdk.MediaSDK.Download_Callback;

public class LiveMediaRecoder {
    
    private static final String TAG = LiveMediaRecoder.class.getSimpleName();

    private PPboxSink mCapture;

    private String mOutputPath;

    public LiveMediaRecoder(Context ctx, Camera camera) {

        PPboxSink.init(ctx.getApplicationContext());
        mCapture = new PPboxSink(camera);
    }

    public void setOutputPath(String url) {
        mOutputPath = url;
    }

    public void setOnErrorListener(final OnErrorListener listener) {
        mCapture.setDownloadCallback(new Download_Callback() {
            
            @Override
            public void invoke(long err) {
                Log.d(TAG, "error: " + err);
                
                if (null != listener) {
                    listener.onError();
                }
            }
        });
    }

    public void start() {
        mCapture.open(mOutputPath);
        mCapture.start();
    }

    public void stop() {
        mCapture.stop();
        mCapture.close();
    }

    public interface OnErrorListener {
        void onError();
    }
}
