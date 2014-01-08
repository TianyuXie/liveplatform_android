package com.pplive.liveplatform.dac.stat;

import java.text.MessageFormat;

import com.pplive.liveplatform.dac.data.PublishData;

public class PublishDacStat extends MediaDacStat implements PublishData {

    private static final long serialVersionUID = 7102952405135080314L;
    
    public PublishDacStat() {
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
    
    public void setPublishStyle(boolean isPreLive) {
        addValueItem(KEY_PUBLISH_STYLE, isPreLive ? PUBLISH_STYLE_PRE : PUBLISH_STYLE_DIRECT);
    }
}
