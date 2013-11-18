package com.pplive.liveplatform.ui.record;

import com.pplive.liveplatform.Constant;

import android.content.Context;
import android.hardware.Camera;

public class LiveMediaRecoder {

    private PPboxSink mCapture;

    public LiveMediaRecoder(Context ctx, Camera camera) {

        PPboxSink.init(ctx.getApplicationContext());
        mCapture = new PPboxSink(camera);

        //        String url = "rtmp://183.129.205.101:1936/push/mobi1";
        //        String url = "rtmp://192.168.27.253/live/android";
        //        String url = "/sdcard/pplog/a.flv";

        String url = Constant.TEST_PUSH_URL;

        mCapture.open(url);
    }

    public void start() {
        if (null != mCapture) {

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
