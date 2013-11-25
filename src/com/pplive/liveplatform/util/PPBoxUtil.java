package com.pplive.liveplatform.util;

import java.net.URLEncoder;

import com.pplive.liveplatform.core.rest.Protocol;
import com.pplive.liveplatform.core.rest.URL;

public class PPBoxUtil {

    @SuppressWarnings("deprecation")
    public static URL getSDKPlayString(String playlink) {
        
        URL url = new URL(Protocol.RTSP, "127.0.0.1", 5054, "/record.es");
        url.addParameter("playlinke", URLEncoder.encode(playlink));
        
        return url;
    }
    
    private PPBoxUtil() {}
}
