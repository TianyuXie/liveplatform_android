package com.pplive.liveplatform.core.service.live.model;

import java.util.ArrayList;
import java.util.Locale;

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

    public long getProgramId() {
        return pid;
    }

    public String getProtocol() {
        return protocol;
    }

    public Channel[] getChannels() {
        return channels;
    }

    public Channel getChannel(final int ft) {
        for (Channel channel : getChannels()) {
            if (ft == channel.getFt()) {
                return channel;
            }
        }

        return null;
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
                    url = getLive2M3U8PlayURL(pid, channel.getFt(), channel.getName(), addr, now / 1000, delay, interval, channel.getPath());
                } else if ("rtmp".equals(protocol)) {
                    url = getRtmpPlayURL(protocol, addr, channel.getPath(), channel.getName());
                }
            }

            if (!TextUtils.isEmpty(url)) {
                list.add(url);
            }

        }

        return list;
    }

    public String getLive2M3U8PlayURL(long pid, int ft, String name, String addr, long now, long delay, long interval, String path) {
        String playLink = String.format(Locale.US,
                "%d?ft=%d&name=%s&svrhost=%s&svrtime=%d&delaytime=%d&bitrate=400&interval=%d&bwtype=-1&sdkmode=1&livepath=%s", pid, ft, name, addr, now, delay,
                interval, path);
        return PPBoxUtil.getLive2M3U8PlayURL(playLink).toString();
    }

    public String getRtmpPlayURL(String protocol, String addr, String path, String name) {
        return String.format(Locale.US, "%s://%s/%s/%s");
    }
}
