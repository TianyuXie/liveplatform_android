package com.pplive.liveplatform.core.service.live.model;

import com.pplive.liveplatform.util.StringUtil;

public class WatchList {
    
    Recommend recommend;
    
    java.util.List<Watch> medias;
    
    public Watch getRecommendedWatch() {
        
        if (null != recommend && !StringUtil.isNullOrEmpty(recommend.protocol)) {
            for (Watch watch : medias) {
                if (recommend.protocol.equals(watch.getProtocol()) && null != watch.getChannel(recommend.ft)) {
                    return watch;
                }
            }
        }
        
        return null;
    }

    class Recommend {
        
        String protocol;
        
        int ft;
    }
}
