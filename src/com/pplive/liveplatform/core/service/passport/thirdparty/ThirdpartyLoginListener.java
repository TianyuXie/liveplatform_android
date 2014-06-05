package com.pplive.liveplatform.core.service.passport.thirdparty;

import com.pplive.liveplatform.core.service.passport.model.LoginResult;

public interface ThirdpartyLoginListener {
    void onLoginSuccess(LoginResult res);

    void onLoginFailed(String message);

    void onLoginCanceled();
};