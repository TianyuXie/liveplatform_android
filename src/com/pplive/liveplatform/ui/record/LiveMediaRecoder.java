package com.pplive.liveplatform.ui.record;

import android.content.Context;
import android.hardware.Camera;

public class LiveMediaRecoder {

    private PPboxSink mCapture;
    
    private String mOutputPath;

    public LiveMediaRecoder(Context ctx, Camera camera) {

        PPboxSink.init(ctx.getApplicationContext());
        mCapture = new PPboxSink(camera);
    }
    
    public void setOutputPath(String url) {
        mOutputPath = url;
    }

    public void start() {
        if (null != mCapture) {
            mCapture.open(mOutputPath);
            mCapture.start();
        }
    }

    public void stop() {
        if (null != mCapture) {
            mCapture.stop();
            mCapture.close();
        }
    }
}
