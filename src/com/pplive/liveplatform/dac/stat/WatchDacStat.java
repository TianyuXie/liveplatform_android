package com.pplive.liveplatform.dac.stat;

import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.dac.data.WatchData;

public class WatchDacStat extends MediaDacStat implements WatchData {

    private static final long serialVersionUID = -8496347648846165725L;
    
    private long mLastSeekTime = -1;
    
    private long mLastBufferingStartTime = -1;
    
    private long mBufferingTime = 0;
    
    private long mBufferingCount = 0;
    
    private long mDraggingBufferingTime = 0;
    
    private long mDraggingCount = 0;
    
    private boolean mByDragging = false;
    
    public WatchDacStat() {
        addMetaItem(KEY_LOG_KIND, LOG_KIND_WATCH);
        addValueItem(KEY_WATCH_TYPE, WATCH_TYPE_UNKNOWN);
    }

    public void setWatchType(boolean isVOD) {
        addValueItem(KEY_WATCH_TYPE, isVOD ? WATCH_TYPE_LIVE_VOD : WATCH_TYPE_LIVE);
    }

    public void setPlayProtocol(Watch.Protocol protocol) {
        int play_protocol = PLAY_PROTOCOL_UNKNONW;
        switch (protocol) {
        case RTMP:
            play_protocol = PLAY_PROTOCOL_RTMP;
            break;
        case LIVE2:
            play_protocol = PLAY_PROTOCOL_LIVE2;
            break;
        default:
            break;
        }
        
        addValueItem(KEY_PLAY_PROTOCOL, play_protocol);
    }
    
    public void onSeek() {
        mLastSeekTime = System.currentTimeMillis();
        
        addValueItem(KEY_DRAGGING_COUNT, mDraggingCount);
    }
    
    public void onBufferStart() {
        long current_time = System.currentTimeMillis();
        
        if (mLastBufferingStartTime == -1) {
            mLastBufferingStartTime = current_time;
        }
        
        addValueItem(KEY_PLAYING_BUFFER_COUNT, ++mBufferingCount);
        
        if (mLastSeekTime > 0 && current_time - mLastSeekTime < 3000 /* millisecond */) {
            mByDragging = true;
        }
    }
    
    public void onBufferEnd() {
        if (mLastBufferingStartTime > 0) {
            long current_time = System.currentTimeMillis();
            
            mBufferingTime += (current_time - mLastBufferingStartTime);
            addValueItem(KEY_PLAYING_BUFFER_TIME, mBufferingTime);
            
            if (mByDragging) {
                mDraggingBufferingTime += (current_time - mLastBufferingStartTime);
                addValueItem(KEY_DRAGGING_BUFFER_TIME, mDraggingBufferingTime);
            }
        }
        
        mLastBufferingStartTime = -1;
        mByDragging = false;
    }
    
    @Override
    public void onPlayStop() {
        super.onPlayStop();
        
        onBufferEnd();
    }
}
