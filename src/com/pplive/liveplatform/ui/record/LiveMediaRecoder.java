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

    public void setOnErrorListener(OnErrorListener listener) {
        mCapture.setOnErrorListener(listener);
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
