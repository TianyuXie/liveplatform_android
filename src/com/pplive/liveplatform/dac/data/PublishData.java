package com.pplive.liveplatform.dac.data;

public interface PublishData extends MediaData {

    static final String KEY_BIT_RATE = "BR";
    
    static final String KEY_VIDEO_RESOLUTION = "VR";
    
    static final String KEY_VIDEO_FPS = "FS";
    
    static final String KEY_PUBLISH_STYLE = "PS";
    
    static final String KEY_PUBLISH_MODE = "PK";
    
    static final String KEY_PAUSE_COUNT = "IC";
    
    static final String KEY_PAUSE_TIME = "IT";
    
    static final String KEY_PREVIEW_TIME = "PRT";
    
    static final int PUBHLISH_STYLE_UNKNOWN = -1;
    
    static final int PUBLISH_STYLE_PRE = 0;
    
    static final int PUBLISH_STYLE_DIRECT = 1;
    
    static final int PUBLISH_MODE_CAMERA = 0;
    
    static final int PUBLISH_MODE_XSPLIT = 1;
    
    static final int PUBLISH_MODE_SERVER = 2;
}
