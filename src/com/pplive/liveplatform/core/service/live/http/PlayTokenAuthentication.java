package com.pplive.liveplatform.core.service.live.http;

import android.text.TextUtils;

public class PlayTokenAuthentication extends TokenAuthentication {

    public PlayTokenAuthentication(String playToken) {
        this(null, playToken);
    }
    
    public PlayTokenAuthentication(String coToken, String playToken) {
        super(coToken);
        
        if (!TextUtils.isEmpty(playToken)) {
            mKeyValueMap.put(KEY_PLAY_TOKEN, playToken);
        }
    }
}
