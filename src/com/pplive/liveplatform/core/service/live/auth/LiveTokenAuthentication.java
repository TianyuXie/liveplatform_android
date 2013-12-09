package com.pplive.liveplatform.core.service.live.auth;

import android.text.TextUtils;

public class LiveTokenAuthentication extends UserTokenAuthentication {
    
    public LiveTokenAuthentication(String liveToken) {
        this(null, liveToken);
    }
    
    public LiveTokenAuthentication(String coToken, String liveToken) {
        super(coToken);
        
        if (!TextUtils.isEmpty(liveToken)) {
            mKeyValueMap.put(KEY_LIVE_TOKEN, liveToken);
        }
    }
}
