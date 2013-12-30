package com.pplive.liveplatform.core.service.live.model;

import java.util.ArrayList;
import java.util.Locale;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.util.PPBoxUtil;

public class Watch {

    public enum Protocol {
        
        @SerializedName("live2")
        LIVE2, 
        
        @SerializedName("rtmp")
        RTMP;
    }

    long pid;

    Protocol protocol;

    long delay;

    Channel[] channels;

    int interval;

    long starttime;

    long endtime;

    long now;

    public long getProgramId() {
        return pid;
    }

    public Protocol getProtocol() {
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
                if (Protocol.LIVE2 == protocol) {
                    //                    url = getLive2LiveM3U8PlayURL(pid, channel.getFt(), channel.getName(), addr, starttime / 1000, 0, interval, channel.getPath());
                    url = getLive2VODM3U8PlayURL(pid, channel.getFt(), channel.getName(), addr, now / 1000, 0, interval, channel.getPath(), starttime / 1000, endtime / 1000);
                } else if (Protocol.RTMP == protocol) {
                    url = getRtmpPlayURL(addr, channel.getPath(), channel.getName());
                }
            }

            if (!TextUtils.isEmpty(url)) {
                list.add(url);
            }

        }

        return list;
    }

    public String getLive2LiveM3U8PlayURL(int ft) {

        for (Channel channel : channels) {
            if (ft == channel.getFt() && channel.addr.length > 0) {
                return getLive2LiveM3U8PlayURL(pid, ft, channel.getName(), channel.addr[0], now / 1000, interval, delay, channel.getPath());
            }
        }

        return null;
    }

    public String getLive2LiveM3U8PlayURL(long pid, int ft, String name, String addr, long now, long delay, long interval, String path) {
        String playLink = String.format(Locale.US,
                "%d?ft=%d&name=%s&svrhost=%s&svrtime=%d&delaytime=%d&bitrate=400&interval=%d&bwtype=0&sdkmode=0&livepath=%s", pid, ft, name, addr, now, delay,
                interval, path);
        return PPBoxUtil.getLive2M3U8PlayURL(playLink).toString();
    }

    public String getLive2VODM3U8PlayURL(int ft) {
        for (Channel channel : channels) {
            if (ft == channel.getFt() && channel.addr.length > 0) {
                return getLive2VODM3U8PlayURL(pid, ft, channel.getName(), channel.addr[0], now / 1000, interval, 0, channel.getPath(), starttime / 1000,
                        endtime / 1000);
            }
        }

        return null;
    }

    public String getLive2VODM3U8PlayURL(long pid, int ft, String name, String addr, long now, long delay, long interval, String path, long begin, long end) {
        String playLink = String.format(Locale.US,
                "%d?ft=%d&name=%s&svrhost=%s&svrtime=%d&delaytime=%d&bitrate=400&interval=%d&bwtype=0&sdkmode=0&livepath=%s&begin_time=%d&end_time=%d", pid,
                ft, name, addr, now, delay, interval, path, begin, end);
        return PPBoxUtil.getLive2M3U8PlayURL(playLink).toString();
    }

    public String getRtmpPlayURL(int ft) {
        for (Channel channel : channels) {
            if (ft == channel.getFt() && channel.addr.length > 0) {
                return getRtmpPlayURL(channel.addr[0], channel.getPath(), channel.getName());
            }
        }

        return null;
    }

    public String getRtmpPlayURL(String addr, String path, String name) {
        return String.format(Locale.US, "rtmp://%s%s/%s", addr, path, name);
    }
}
