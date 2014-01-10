package com.pplive.liveplatform.dac.stat;

import java.util.UUID;

import android.util.Log;

import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.dac.data.MediaData;
import com.pplive.liveplatform.net.NetworkManager;

public abstract class MediaDacStat extends BaseDacStat implements MediaData {

    private static final long serialVersionUID = -7272971189395559240L;

    private long mStartTime = -1;

    private int mReplayCount = 0;

    public MediaDacStat() {
        addValueItem(KEY_VV_ID, UUID.randomUUID());
        addValueItem(KEY_PROGRAM_ID, UNKNOWN_INT);
        addValueItem(KEY_PROGRAM_TITLE, UNKNOWN_STRING);
        addValueItem(KEY_PROGRAM_SUBJECT_ID, UNKNOWN_INT);
        addValueItem(KEY_IS_SUCCESS, IS_SUCCESS_FALSE);
        addValueItem(KEY_PLAY_START_TIME, UNKNOWN_INT);
        addValueItem(KEY_PLAY_TIME, UNKNOWN_INT);
        addValueItem(KEY_PLAY_START_DELAY, UNKNOWN_INT);
        addValueItem(KEY_MEDIA_SVC_DELAY, UNKNOWN_INT);
        addValueItem(KEY_PLAYING_BUFFER_TIME, 0);
        addValueItem(KEY_PLAYING_BUFFER_COUNT, 0);
        addValueItem(KEY_RELPLAY_COUNT, 0);
        addValueItem(KEY_DRAGGING_BUFFER_TIME, 0);
        addValueItem(KEY_DRAGGING_COUNT, 0);
        addValueItem(KEY_SDK_RUNNING, SDK_RUNNING_FALSE);
        addValueItem(KEY_PLAY_PROTOCOL, PLAY_PROTOCOL_UNKNONW);
        addValueItem(KEY_ACCESS_TYPE, ACCESS_TYPE_UNKNOWN);
        addValueItem(KEY_SERVER_ADDRESS, ACCESS_TYPE_UNKNOWN);
    }

    public void setProgramInfo(Program program) {
        addValueItem(KEY_PROGRAM_ID, program.getId());
        addValueItem(KEY_PROGRAM_TITLE, program.getTitle());
        addValueItem(KEY_PROGRAM_SUBJECT_ID, program.getSubjectId());
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
        case FAST_MOBILE:
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

    public void setPlayStartTime(long start_time) {
        addValueItem(KEY_PLAY_START_TIME, start_time);
    }

    public void addReplayCount() {
        addValueItem(KEY_RELPLAY_COUNT, ++mReplayCount);
    }

    public void onPlayStart() {
        mStartTime = System.currentTimeMillis();
        
        Log.d(TAG, "onPlayStart: " + mStartTime);
    }

    public void onPlayRealStart() {
        if (mStartTime > 0) {
            addValueItem(KEY_PLAY_START_DELAY, System.currentTimeMillis() - mStartTime);
        }
    }

    public void onMediaServerResponse() {
        if (mStartTime > 0) {
            addValueItem(KEY_MEDIA_SVC_DELAY, System.currentTimeMillis() - mStartTime);
        }
    }

    public void onPlayStop() {
        if (mStartTime > 0) {
            addValueItem(KEY_PLAY_TIME, System.currentTimeMillis() - mStartTime);
            mStartTime = -1;
        }
    }
}
