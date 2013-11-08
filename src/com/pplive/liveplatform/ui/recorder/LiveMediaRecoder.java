package com.pplive.liveplatform.ui.recorder;

import android.content.Context;
import android.hardware.Camera;

public class LiveMediaRecoder {

    private PPboxSink mCapture;

    public LiveMediaRecoder(Context ctx, Camera camera) {

        PPboxSink.init(ctx.getApplicationContext());
        mCapture = new PPboxSink(camera);

        //String url = "rtmp://192.168.27.253/live/android?format=rtm&mux.Muxer.video_codec=AVC1&mux.Muxer.audio_codec=MP4A&mux.Encoder.AVC1.param={preset:veryfast,tune:zerolatency,profile:baseline}";
        String url = "rtmp://192.168.27.253/live/android";
//        String url = "/sdcard/pplog/a.flv";
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
