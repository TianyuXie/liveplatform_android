package com.pplive.liveplatform.dac.data;

public interface PublishWatchData extends CommonData {

    static final String KEY_VV_ID = "VV";
    
    static final String KEY_PROGRAM_ID = "G";
    
    static final String KEY_PROGRAM_TITLE = "PID";
    
    static final String KEY_PROGRAM_SUBJECT_ID = "GS";
    
    static final String KEY_IS_SUCCESS = "S";
    
    static final String KEY_PLAY_START_TIME = "ST";
    
    static final String KEY_PLAY_TIME = "I";
    
    static final String KEY_PLAY_START_DELAY = "L";
    
    static final String KEY_MEDIA_SVC_DELAY = "ML";
    
    static final String KEY_PLAYING_BUFFER_TIME = "BT";
    
    static final String KEY_PLAYING_BUFFER_COUNT = "BC";
    
    static final String KEY_RELPLAY_COUNT = "RC";
    
    static final String KEY_DRAGGING_BUFFER_TIME = "DT";
    
    static final String KEY_DRAGGING_COUNT = "DC";
    
    static final String KEY_SDK_RUNNING = "PK";
    
    static final String KEY_PLAY_PROTOCOL = "PP";
    
    static final String KEY_ACCESS_TYPE = "R";
    
    static final String KEY_SERVER_ADDRESS = "SA";
    
    static final int IS_SUCCESS_FALSE = 0;
    
    static final int IS_SUCCESS_TRUE = 1;
    
    static final int SDK_RUNNING_FALSE = 0;
    
    static final int SDK_RUNNING_TRUE = 1;
    
    static final int ACCESS_TYPE_LAN = 0;
    
    static final int ACCESS_TYPE_WIFI = 1;
    
    static final int ACCESS_TYPE_3G = 2;
}
