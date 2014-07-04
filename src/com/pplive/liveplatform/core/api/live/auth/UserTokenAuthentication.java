package com.pplive.liveplatform.core.api.live.auth;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.springframework.http.HttpAuthentication;

import com.pplive.liveplatform.Constants;

import android.text.TextUtils;

public class UserTokenAuthentication extends HttpAuthentication {

    protected static final String KEY_CO_NAME = "coname";
    protected static final String KEY_CO_TOKEN = "cotk";
    protected static final String KEY_PLAY_TOKEN = "playtk";
    protected static final String KEY_LIVE_TOKEN = "livetk";
    
    protected static final String DEFAULT_CO_NAME = Constants.DEFAULT_CONAME_PPTV; 
    
    protected LinkedHashMap<String, String> mKeyValueMap = new LinkedHashMap<String, String>();
    
    public UserTokenAuthentication(String coToken) {
        this(DEFAULT_CO_NAME, coToken);
    }
    
    private UserTokenAuthentication(String coName, String coToken) {
        if (!TextUtils.isEmpty(coName)) {
            mKeyValueMap.put(KEY_CO_NAME, coName);
        }
        
        if (!TextUtils.isEmpty(coToken)) {
            mKeyValueMap.put(KEY_CO_TOKEN, coToken);
        }
    }
    
    @Override
    public String getHeaderValue() {
        StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        for (Entry<String, String> entry : mKeyValueMap.entrySet()) {
            sb.append(String.format(first ? "%s=%s" : ";%s=%s", entry.getKey(), entry.getValue()));
            first = false;
        }
        
        return sb.toString();
    }
}
