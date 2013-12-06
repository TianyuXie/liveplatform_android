package com.pplive.liveplatform.util;

import java.io.File;
import java.net.URLEncoder;

import android.content.Context;

import com.pplive.liveplatform.core.service.URL;
import com.pplive.sdk.MediaSDK;

public class PPBoxUtil {
    
    private static final String PPBOX_HOST = "127.0.0.1";
    
    private static final int PPBOX_RTSP_PORT = 5054;
    
    private static final int PPBOX_PLAY_PORT = 9006;
    
    public static void initPPBox(Context ctx) {
        File cacheDirFile = ctx.getCacheDir();
        String dataDir = cacheDirFile.getParentFile().getAbsolutePath();
        String libDir = dataDir + "/lib";
        String tmpDir = cacheDirFile.getAbsolutePath();
        File tmpDirFile = new File(tmpDir);
        tmpDirFile.mkdir();

        MediaSDK.libPath = libDir;
        MediaSDK.logPath = tmpDir;
        MediaSDK.logLevel = MediaSDK.LEVEL_EVENT;
    }
    
    public static void startPPBox() {
        MediaSDK.startP2PEngine("161", "12", "111");
    }
    
    public static URL getRtmpM3U8PlayURL(String playLink) {
        return getRtmpM3U8PlayURL(playLink, PPBOX_PLAY_PORT);
    }
    
    @SuppressWarnings("deprecation")
    public static URL getRtmpM3U8PlayURL(String playLink, int port) {
        
        URL url = new URL(URL.Protocol.HTTP, PPBOX_HOST, PPBOX_PLAY_PORT, "/record.m3u8");
        url.addParameter("playlink", URLEncoder.encode(playLink));
        url.addParameter("mux.M3U8.segment_duration", 5);
        url.addParameter("mux.M3U8.back_seek_time", 0);
        
        return url;
    }
    
    public static URL getPPLive2M3U8PlayURL(String playLink) {
        return getPPLive2M3U8PlayURL(playLink, PPBOX_PLAY_PORT);
    }
    
    @SuppressWarnings("deprecation")
    public static URL getPPLive2M3U8PlayURL(String playLink, int port) {
        
        URL url = new URL(URL.Protocol.HTTP, PPBOX_HOST, port, "/play.m3u8");
        url.addParameter("type", "pplive3");
        url.addParameter("playlink", URLEncoder.encode(playLink));
        
        return url;
    }
    
    
    private PPBoxUtil() {}
}
