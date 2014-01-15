package com.pplive.liveplatform.dac.data;

public interface WatchData extends MediaData {

    static final String KEY_WATCH_TYPE = "WK";
    
    static final int WATCH_TYPE_UNKNOWN = UNKNOWN_INT;
    
    static final int WATCH_TYPE_LIVE = 0;
    
    static final int WATCH_TYPE_LIVE_VOD = 1;
    
    static final int WATCH_TYPE_VOD = 2;
}
