package com.pplive.liveplatform.util;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.pplive.liveplatform.util.URL.Protocol;
import com.pplive.sdk.MediaSDK;

public class PPBoxUtil {

    private static final String TAG = PPBoxUtil.class.getSimpleName();

    static final String PPBOX_HOST = "127.0.0.1";

    static final int PPBOX_RTSP_PORT = 5054;

    static final int PPBOX_HTTP_PORT = 9006;

    static long sSerialNum = 0;

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

        MediaSDK.setConfig("", "HttpManager", "addr", "0.0.0.0:9006+");
        MediaSDK.setConfig("", "RtspManager", "addr", "0.0.0.0:5054+");
    }

    public static long startPPBox() {
        return MediaSDK.startP2PEngine("161", "12", "111");
    }

    public static boolean isSDKRuning() {
        long ret = startPPBox();
        return -1 != ret && 9 != ret;
    }

    public static URL getRtmpM3U8PlayURL(String playLink) {
        return getRtmpM3U8PlayURL(playLink, MediaSDK.getPort("http"));
    }

    public static URL getRtmpM3U8PlayURL(String playLink, int port) {

        URL url = new URL(URL.Protocol.HTTP, PPBOX_HOST, port, "/record.m3u8");
        url.addParameter("playlink", URLUtil.encode(playLink));
        url.addParameter("mux.M3U8.segment_duration", 5);
        url.addParameter("mux.M3U8.back_seek_time", 0);
        url.addParameter("realtime", "high");
        url.addParameter("serialnum", sSerialNum);

        return url;
    }

    public static URL getLive2M3U8PlayURL(String playLink) {
        return getPPLive2M3U8PlayURL(playLink, MediaSDK.getPort("http"));
    }

    public static URL getPPLive2M3U8PlayURL(String playLink, int port) {

        URL url = new URL(URL.Protocol.HTTP, PPBOX_HOST, port, "/record.m3u8");
        url.addParameter("type", "pplive3");
        url.addParameter("playlink", URLUtil.encode(playLink));
        url.addParameter("realtime", "low");
        url.addParameter("serialnum", sSerialNum);

        return url;
    }

    public static void closeM3U8() {
        final URL url = new URL(Protocol.HTTP, PPBOX_HOST, MediaSDK.getPort("http"), "/close");
        url.addParameter("serialnum", sSerialNum++);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                HttpGet get = new HttpGet(url.toString());

                HttpClient client = new DefaultHttpClient();

                try {
                    client.execute(get);
                } catch (ClientProtocolException e) {
                    Log.w(TAG, e.toString());
                } catch (IOException e) {
                    Log.w(TAG, e.toString());
                }
            }
        });

        t.start();
    }

    private PPBoxUtil() {
    }
}
