package com.pplive.liveplatform.vo.program;

public class Program {

    enum Mode {
        CAMERA,
        XSPLIT,
        RTMP
    }
    
    enum WatchProtocol {
        LIVE2,
        RTMP
    }
    
    long mPid;
    
    Mode mMode;
    
    String mExternalUrl;
    
    WatchProtocol[] mWatchProtocls;
    
    LiveStatus.Status mLiveStatus;
    
    String[] mTags;
    
    String mCoverUrl;
    
    String mScreenshotUrl;
    
    // TODO: Exts mExts
    
    long mStartTime;
    
    long mEndTime;
    
    long mInsertTime;
    
    long mLastUpdateTime;
    
    // TODO: ProgramRtmp

    // TODO: ProgramLive2 
    
    int mSubjectId;
    
    String mCoName;
    
    String mOwner;
    
    String mTitle;
}
