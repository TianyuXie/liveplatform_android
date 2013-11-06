package com.pplive.liveplatform.net;


import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;

public class URL implements Serializable {

    private static final long serialVersionUID = -3649683692013310693L;
    
    public static final String HTTP = "http://";
    
    public static final String HTTPS = "https://";
    
    public static final String RTMP = "rtmp://";
    
    public static final String RTSP = "rtsp://";
    
    public static final String CONTENT = "content://";
    
    public static final String FILE = "file://";
    
    private final String mProtocol;
    
    private final String mHost;
    
    private final int mPort;
    
    private final String mPath;
    
    private final Map<String, Object> mParams;
    
    public URL() {
        this(null);
    }
    
    public URL(String host) {
        this(host, null);
    }
    
    public URL(String host, String path) {
        this(host, -1, path);
    }
    
    public URL(String host, int port, String path) {
        this(null, host, port, path);
    }
    
    public URL(String protocol, String host, String path) {
        this(protocol, host, -1, path);
    }
    
    public URL(String protocol, String host, int port, String path) {
        mProtocol = protocol;
        mHost = host;
        mPort = port;
        mPath = path;
        
        mParams = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
    }
    
    public final <T> void addParameter(String key, T value) {
        mParams.put(key, value);
    }
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(); 
        
        if (!TextUtils.isEmpty(mProtocol)) {
            sb.append(mProtocol);
        }
        
        if (!TextUtils.isEmpty(mHost)) {
            sb.append(mHost);
        }
        
        if (mPort > 0) {
            sb.append(String.format(":%d", mPort));
        }
        
        if (!TextUtils.isEmpty(mPath)) {
            sb.append(mPath);
        }
        
        boolean first = false;
        for (Entry<String, Object> entry : mParams.entrySet()) {
            sb.append(String.format(first ? "?%s=%s" : "&%s=%s", entry.getKey(), entry.getValue()));
        }
        
        return sb.toString();
    }
    
}
