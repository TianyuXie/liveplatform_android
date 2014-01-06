package com.pplive.liveplatform.dac.data;

public interface WatchData extends PublishWatchData {

    static final String KEY_WATCH_TYPE = "WK";
    
    static final int WATCH_TYPE_LIVE = 0;
    
    static final int WATCH_TYPE_LIVE_VOD = 1;
    
    static final int WATCH_TYPE_VOD = 2;
}
