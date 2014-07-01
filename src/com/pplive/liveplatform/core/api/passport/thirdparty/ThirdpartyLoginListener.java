package com.pplive.liveplatform.core.api.passport.thirdparty;

import com.pplive.liveplatform.core.api.passport.model.LoginResult;

public interface ThirdpartyLoginListener {
    void onLoginSuccess(LoginResult res);

    void onLoginFailed(String message);

    void onLoginCanceled();
};