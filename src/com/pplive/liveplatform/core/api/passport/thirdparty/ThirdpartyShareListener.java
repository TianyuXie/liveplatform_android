package com.pplive.liveplatform.core.api.passport.thirdparty;


public interface ThirdpartyShareListener {
    void shareSuccess();

    void shareFailed(String message);

    void shareCanceled();
};