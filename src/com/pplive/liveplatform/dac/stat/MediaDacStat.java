package com.pplive.liveplatform.dac.stat;

import java.util.UUID;

import com.pplive.liveplatform.dac.data.MediaData;
import com.pplive.liveplatform.net.NetworkManager;

public abstract class MediaDacStat extends BaseDacStat implements MediaData {

    private static final long serialVersionUID = -7272971189395559240L;

    public MediaDacStat() {
        addValueItem(KEY_VV_ID, UUID.randomUUID());
        addValueItem(KEY_PROGRAM_ID, UNKNOWN_INT);
        addValueItem(KEY_PROGRAM_TITLE, UNKNOWN_STRING);
        addValueItem(KEY_PROGRAM_SUBJECT_ID, UNKNOWN_INT);
        addValueItem(KEY_PLAY_START_TIME, UNKNOWN_INT);
        addValueItem(KEY_PLAY_TIME, UNKNOWN_INT);
        addValueItem(KEY_PLAY_START_DELAY, 0);
        addValueItem(KEY_MEDIA_SVC_DELAY, 0);
        addValueItem(KEY_PLAYING_BUFFER_TIME, 0);
        addValueItem(KEY_PLAYING_BUFFER_COUNT, 0);
        addValueItem(KEY_DRAGGING_BUFFER_TIME, 0);
        addValueItem(KEY_DRAGGING_COUNT, 0);
        addValueItem(KEY_SDK_RUNNING, SDK_RUNNING_FALSE);
        addValueItem(KEY_PLAY_PROTOCOL, PLAY_PROTOCOL_UNKNONW);
        addValueItem(KEY_ACCESS_TYPE, ACCESS_TYPE_UNKNOWN);
        addValueItem(KEY_SERVER_ADDRESS, ACCESS_TYPE_UNKNOWN);
    }

    public void setProgramId(long pid) {
        addValueItem(KEY_PROGRAM_ID, pid);
    }

    public void setProgramTitle(String title) {
        addValueItem(KEY_PROGRAM_TITLE, title);
    }

    public void setProgramSubjectId(int subjectId) {
        addValueItem(KEY_PROGRAM_SUBJECT_ID, subjectId);
    }

    public void setIsSuccess(boolean success) {
        addValueItem(KEY_IS_SUCCESS, success ? IS_SUCCESS_TRUE : IS_SUCCESS_FALSE);
    }

    public void setSDKRunning(boolean running) {
        addValueItem(KEY_SDK_RUNNING, running ? SDK_RUNNING_TRUE : SDK_RUNNING_FALSE);
    }

    public void setAccessType(NetworkManager.NetworkState state) {
        int access_type = ACCESS_TYPE_UNKNOWN;
        switch (state) {
        case WIFI:
            access_type = ACCESS_TYPE_WIFI;
            break;
        case THIRD_GENERATION:
            access_type = ACCESS_TYPE_3G;
            break;
        default:
            break;
        }

        addValueItem(KEY_ACCESS_TYPE, access_type);
    }
    
    public void setServerAddress(String address) {
        addValueItem(KEY_SERVER_ADDRESS, address);
    }
}
