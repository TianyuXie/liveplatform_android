package com.pplive.liveplatform.core.rest.model;

import java.util.ArrayList;

import com.pplive.liveplatform.util.PPBoxUtil;

public class Watch {

    long pid;
    
    String protocol;
    
    long delay;
    
    Channel[] channels;
    
    public long getId() {
        return pid;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public Channel[] getChannels() {
        return channels;
    }
    
    public java.util.List<String> getWatchStringList() {
        return getWatchStringList(true);
    }
    
    public java.util.List<String> getWatchStringList(boolean rtsp) {
        if (null == channels || 0 == channels.length) {
            return null;
        }
        
        java.util.List<String> list = new ArrayList<String>(channels.length);
        
        for (Channel channel : channels) {
            for (String fullPath : channel.getFullPathList()) {
                String url = protocol + "://" + fullPath;
                
                url = !rtsp ? url : PPBoxUtil.getSDKPlayString(url).toString();
                
                list.add(url);
            }
        }
        
        return list;
    }
}
