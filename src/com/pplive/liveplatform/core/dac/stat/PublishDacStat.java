package com.pplive.liveplatform.core.dac.stat;

import java.text.MessageFormat;

import android.util.Log;

import com.pplive.liveplatform.core.dac.data.PublishData;

public class PublishDacStat extends MediaDacStat implements PublishData {

    private static final long serialVersionUID = 7102952405135080314L;
    
    private long mLastPauseStartTime = -1;
    
    private long mPauseTime = 0;
    
    private int mPauseCount = 0;
    
    public PublishDacStat() {
        addMetaItem(KEY_LOG_KIND, LOG_KIND_PUBLISH);
        addValueItem(KEY_BIT_RATE, UNKNOWN_INT);
        addValueItem(KEY_VIDEO_RESOLUTION, UNKNOWN_STRING);
        addValueItem(KEY_VIDEO_FPS, UNKNOWN_INT);
        addValueItem(KEY_PUBLISH_STYLE, UNKNOWN_INT);
        addValueItem(KEY_PUBLISH_MODE, PUBLISH_MODE_CAMERA);
        addValueItem(KEY_PAUSE_TIME, 0);
        addValueItem(KEY_PAUSE_COUNT, 0);
        addValueItem(KEY_PREVIEW_TIME, 0);
    }
    
    public void setBitrate(long bitrate) {
        addValueItem(KEY_BIT_RATE, bitrate);
    }

    public void setVideoResolution(int height, int width) {
        addValueItem(KEY_VIDEO_RESOLUTION, MessageFormat.format("{0,number,#}*{1,number,#}", height, width));
    }
    
    public void setVideoFPS(int fps) {
        addValueItem(KEY_VIDEO_FPS, fps);
    }
    
    public void setPublishStyle(boolean isPreLive) {
        addValueItem(KEY_PUBLISH_STYLE, isPreLive ? PUBLISH_STYLE_PRE : PUBLISH_STYLE_DIRECT);
    }
    
    public void onPauseStart() {
        Log.d(TAG, "onPauseStart");
        
        if (mLastPauseStartTime == -1) {
            mLastPauseStartTime = System.currentTimeMillis();
        }
        
        addValueItem(KEY_PAUSE_COUNT, ++mPauseCount);
    }
    
    public void onPauseEnd() {
        Log.d(TAG, "onPauseEnd");
        
        if (mLastPauseStartTime > 0) {
            mPauseTime += (System.currentTimeMillis() - mLastPauseStartTime);
            addValueItem(KEY_PAUSE_TIME, mPauseTime);
        }
        
        mLastPauseStartTime = -1;
    }
    
    @Override
    public void onPlayStop() {
        super.onPlayStop();
        
        onPauseEnd();
    }
    
}
