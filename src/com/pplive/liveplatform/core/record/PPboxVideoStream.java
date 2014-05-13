package com.pplive.liveplatform.core.record;

import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.sdk.MediaSDK;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PPboxVideoStream extends PPboxStream {

    private Camera mCamera;

    private int mDataChange = 0;

    private int mVideoWidth = -1;

    private int mVideoHeight = -1;

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        private final long time_scale = 1000 * 1000 * 1000;

        private int num_total = 0;

        private int num_drop = 0;

        private long next_time = 5 * time_scale;

        private long last_put_preview_time = System.currentTimeMillis();

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            ++num_total;

            long cur_time = System.currentTimeMillis();
            long time_stamp = System.nanoTime() - mStartTime;

            if (cur_time - last_put_preview_time > mQuality.getInterval()) {
                Log.d(TAG, "interval: " + (cur_time - last_put_preview_time));

                PPboxStream.InBuffer buffer = pop();
                if (buffer == null) {
                    ++num_drop;
                } else {
                    Log.d(TAG, "image size: " + data.length);
                    byte[] dataChange = new byte[data.length];
                    switch (mDataChange) {
                    case DevicesChoose.NV12TONV21:
                        ColorFormat.convertNV21toNV12(data);
                        dataChange = data;
                        break;
                    case DevicesChoose.YV12TOYUV420:
                        ColorFormat.YV12toYUV420PackedSemiPlanar(data, dataChange, mVideoWidth, mVideoHeight);
                        break;
                    case DevicesChoose.YV12TOYUV420NV21:
                        ColorFormat.YV12toYUV420PackedSemiPlanar(data, dataChange, mVideoWidth, mVideoHeight);
                        ColorFormat.convertNV21toNV12(dataChange);
                        break;
                    case DevicesChoose.YV12TOI420:
                        ColorFormat.YV12toYUV420Planar(data, dataChange, mVideoWidth, mVideoHeight);
                        //convertNV21toNV12(dataChange);
                        break;
                    case DevicesChoose.NOCHANGE:
                        dataChange = data;
                    }

                    //                    writeYUV(dataChange);

                    buffer.byte_buffer().put(dataChange);
                    put(time_stamp / 1000, buffer);
                    last_put_preview_time = cur_time;
                }

                if (time_stamp >= next_time) {
                    Log.d(TAG, "video " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: "
                            + num_drop);
                    next_time += 5 * time_scale;
                }
            }

            camera.addCallbackBuffer(data);
        }
    };

    public PPboxVideoStream(long capture, int itrack, long startTime, Camera camera, Quality quality) {
        super(capture, startTime, quality);

        mStreamType = "Video";

        mCaptureId = capture;

        mCamera = camera;

        mQuality = quality;

        Camera.Parameters p = camera.getParameters();

        Log.d(TAG, "Preview Format: " + p.getPreviewFormat());

        Camera.Size size = p.getPreviewSize();
        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            MediaFormat format = MediaManager.getInstance().getSupportedEncodingVideoFormat(MediaManager.MIME_TYPE_VIDEO_AVC, size, mQuality);
            mEncoder = MediaCodec.createEncoderByType(MediaManager.MIME_TYPE_VIDEO_AVC);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        }
        mDataChange = DevicesChoose.getYUV420Switch();

        mInSize = pic_size(p);

        mStreamInfo = new MediaSDK.StreamInfo();

        mStreamInfo.time_scale = 1000 * 1000;
        mStreamInfo.bitrate = 0;
        mStreamInfo.__union0 = p.getPreviewSize().width;
        mStreamInfo.__union1 = p.getPreviewSize().height;
        mStreamInfo.__union2 = mQuality.getFrameRate();
        mStreamInfo.__union3 = 0;

        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            mStreamInfo.format_size = 0;
            mStreamInfo.format_buffer = ByteBuffer.allocateDirect(0);
        }

        mSample = new MediaSDK.Sample();
        mSample.itrack = itrack;
        mSample.flags = 0;
        mSample.time = 0;
        mSample.composite_time_delta = 0;
        mSample.size = mInSize;
        mSample.buffer = null;
    }

    @Override
    public void start() {
        super.start();

        resetCamera(mCamera);
    }

    @Override
    public void stop() {
        mCamera.setPreviewCallbackWithBuffer(null);
        mCamera = null;
    }

    public void resetCamera(Camera camera) {
        mCamera = camera;

        Camera.Size size = camera.getParameters().getPreviewSize();
        mVideoWidth = size.width;
        mVideoHeight = size.height;

        final byte[] video_buffer = new byte[bufferSize()];

        camera.addCallbackBuffer(video_buffer);
        camera.setPreviewCallbackWithBuffer(mPreviewCallback);
    }
}
