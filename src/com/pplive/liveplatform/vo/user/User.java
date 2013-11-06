package com.pplive.liveplatform.vo.user;

public class User {
    
    enum STATUS {
        ALLOWED
    }
    
    String mUserName;
    
    String mNickName;
    
    String mIcon;
    
    String mCoName;
    
    long mBirthday;
    
    long mInsertTime;
    
    long mLastUpdateTime;
}
