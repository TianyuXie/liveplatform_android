package com.pplive.liveplatform.core.api.live.model;

public class WatchList {

    Recommend recommend;

    java.util.List<Watch> medias;

    public java.util.List<Watch> getMedias() {
        return medias;
    }

    public Watch getRecommendedWatch() {
        if (null != recommend && null != recommend.protocol) {
            for (Watch watch : medias) {
                if (recommend.protocol == watch.getProtocol() && null != watch.getChannel(recommend.ft)) {
                    return watch;
                }
            }
        }
        return null;
    }
    
    public long getNowTime() {
        Watch watch = getRecommendedWatch();
        if (watch == null) {
            return -1;
        } else {
            return watch.getNowTime();
        }
    }
    
    public String getRecommendedWatchAddress() {
        Watch watch = getRecommendedWatch();
        
        return null != watch ? watch.getAddress(recommend.ft) : null;
    }

    public Watch.Protocol getRecommendedProtocol() {

        if (null != recommend) {

            return recommend.protocol;
        }

        return null;
    }

    public String getLive2LiveM3U8PlayURL() {
        Watch watch = getRecommendedWatch();

        if (null != watch) {
            return watch.getLive2LiveM3U8PlayURL(recommend.ft);
        }

        return null;
    }

    public String getLive2VODM3U8PlayURL() {
        Watch watch = getRecommendedWatch();

        if (null != watch) {
            return watch.getLive2VODM3U8PlayURL(recommend.ft);
        }

        return null;
    }

    public String getRtmpPlayURL() {
        Watch watch = getRecommendedWatch();

        if (null != watch) {
            return watch.getRtmpPlayURL(recommend.ft);
        }

        return null;
    }

    class Recommend {

        Watch.Protocol protocol;

        int ft;
    }
}
