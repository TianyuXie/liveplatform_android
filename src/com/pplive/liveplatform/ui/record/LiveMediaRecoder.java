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
            String url = "rtmp://172.16.6.31:1936/push/2f06c748a2394c24802892a8e44cea78?ts=1385455982&token=3d4eb073bf919cb30a8f340f9d6f5d20";
            mCapture.open(url);
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
