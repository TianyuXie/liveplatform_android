package com.pplive.liveplatform.core.service.live.model;

import java.util.ArrayList;

import android.text.TextUtils;

import com.pplive.liveplatform.util.PPBoxUtil;

public class Watch {

    long pid;

    String protocol;

    long delay;

    Channel[] channels;

    int interval;
    
    long starttime;
    
    long now;

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
        if (null == channels || 0 == channels.length) {
            return null;
        }

        java.util.List<String> list = new ArrayList<String>(channels.length);

        for (Channel channel : channels) {
            String url = null;
            for (String addr : channel.getAddrs()) {
                if ("live2".equals(protocol)) {
                    String playLink = String.format("%d?ft=%d&name=%s&svrhost=%s&svrtime=%d&delaytime=0&bitrate=400&interval=%d&bwtype=-1&sdkmode=1&livepath=live2", pid, channel.getFt(),
                            channel.getName(), addr, now / 1000, interval);
                    url = PPBoxUtil.getPPLive2M3U8PlayURL(playLink).toString();
                } else if ("rtmp".equals(protocol)) {
                    String playLink = String.format("%s://%s%s/%s", protocol, addr, channel.getPath(), channel.getName());
                    url = PPBoxUtil.getRtmpM3U8PlayURL(playLink).toString();
                }
            }

            if (!TextUtils.isEmpty(url)) {
                list.add(url);
            }

        }

        return list;
    }
}
