package com.pplive.liveplatform.ui.live.record;

import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.pplive.sdk.MediaSDK;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PPboxVideoStream extends PPboxStream {

   public PPboxVideoStream(long capture, int itrack, Camera camera) {
       mStreamType = "Video";

       this.mCaptureId = capture;

       Camera.Parameters p = camera.getParameters();

       p.getPreviewFormat();
       Log.d(TAG, "Preview Format: " + p.getPreviewFormat());

       Camera.Size size = p.getPreviewSize();
       if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
           MediaFormat format = MediaManager.getInstance().getSupportedEncodingVideoFormat(MediaManager.MIME_TYPE_VIDEO_AVC, size);
           mEncoder = MediaCodec.createEncoderByType(MediaManager.MIME_TYPE_VIDEO_AVC);
           mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
       }

       mInSize = pic_size(p);

       mStreamInfo = new MediaSDK.StreamInfo();

       mStreamInfo.time_scale = 1000 * 1000;
       mStreamInfo.bitrate = 0;
       mStreamInfo.__union0 = p.getPreviewSize().width;
       mStreamInfo.__union1 = p.getPreviewSize().height;
       mStreamInfo.__union2 = MediaManager.FRAME_RATE;
       mStreamInfo.__union3 = 0;
       if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
           mStreamInfo.format_size = 0;
           mStreamInfo.format_buffer = ByteBuffer.allocateDirect(0);
       } else {
           mStreamInfo.format_size = 0;
           mStreamInfo.format_buffer = ByteBuffer.allocateDirect(0);
       }

       //        MediaSDK.CaptureSetStream(capture, itrack, mStreamInfo);

       mSample = new MediaSDK.Sample();
       mSample.itrack = itrack;
       mSample.flags = 0;
       mSample.time = 0;
       mSample.composite_time_delta = 0;
       mSample.size = mInSize;
       mSample.buffer = null;
}
}
