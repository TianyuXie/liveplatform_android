package com.pplive.liveplatform.core.service.passport.thirdparty;


public interface ThirdpartyShareListener {
    void shareSuccess();

    void shareFailed(String message);

    void shareCanceled();
};