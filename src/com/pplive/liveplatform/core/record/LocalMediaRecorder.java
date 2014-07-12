package com.pplive.liveplatform.core.record;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;

public class LocalMediaRecorder implements IMediaRecorder {

    private MediaRecorder mMediaRecorder;

    private MediaRecorderListener mMediaRecorderListener;

    private boolean mRecording = false;

    public LocalMediaRecorder(Context ctx, Camera camera, Quality quality) {
        mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mMediaRecorder.setCamera(camera);
        Camera.Size size = camera.getParameters().getPreviewSize();
        mMediaRecorder.setVideoSize(size.width, size.height);

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    }

    @Override
    public void setOutputPath(String path) {
        mMediaRecorder.setOutputFile(path);
    }

    @Override
    public void setQuality(Quality quality) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setMediaRecorderListener(MediaRecorderListener listener) {
        mMediaRecorderListener = listener;
    }

    @Override
    public void start() {
        try {

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            mRecording = true;

            onSuccess();
        } catch (Exception e) {
            onError();
        }
    }

    @Override
    public void stop() {
        mRecording = false;

        mMediaRecorder.stop();
        mMediaRecorder.release();
    }

    @Override
    public boolean isRecording() {
        return mRecording;
    }

    @Override
    public void resetCamera(Camera camera) {
        // TODO Auto-generated method stub

    }

    private void onSuccess() {
        if (null != mMediaRecorderListener) {
            mMediaRecorderListener.onSuccess();
        }
    }

    private void onError() {
        if (null != mMediaRecorderListener) {
            mMediaRecorderListener.onError();
        }
    }
}
