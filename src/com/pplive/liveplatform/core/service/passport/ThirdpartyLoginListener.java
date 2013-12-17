package com.pplive.liveplatform.core.service.passport;

import com.pplive.liveplatform.core.service.passport.model.LoginResult;

public interface ThirdpartyLoginListener
{
    void LoginSuccess(LoginResult res);
    void LoginFailed();
};