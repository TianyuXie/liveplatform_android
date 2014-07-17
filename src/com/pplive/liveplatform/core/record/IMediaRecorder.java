package com.pplive.liveplatform.core.record;

import android.hardware.Camera;

public interface IMediaRecorder {

    public void setOutputPath(String path);
    
    public void setMediaRecorderListener(MediaRecorderListener listener);
    
    public void start();
    
    public void stop();
    
    public boolean isRecording();
    
    public void resetCamera(Camera camera);
}
