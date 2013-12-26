package com.pplive.liveplatform.core.service.passport;

import com.pplive.liveplatform.core.service.passport.model.LoginResult;

public interface ThirdpartyLoginListener {
    void loginSuccess(LoginResult res);

    void loginFailed(String message);
};