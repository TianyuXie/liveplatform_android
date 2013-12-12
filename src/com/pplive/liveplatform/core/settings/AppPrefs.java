package com.pplive.liveplatform.core.settings;

public class AppPrefs {

    private boolean mPreliveNotify;

    private boolean mContentNotify;

    public boolean isPreliveNotify() {
        return mPreliveNotify;
    }

    public void setPreliveNotify(boolean notify) {
        this.mPreliveNotify = notify;
    }

    public boolean isContentNotify() {
        return mContentNotify;
    }

    public void setContentNotify(boolean notify) {
        this.mContentNotify = notify;
    }

}
