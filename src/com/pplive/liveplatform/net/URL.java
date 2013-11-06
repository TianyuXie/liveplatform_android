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
    
    public static final String CONTENT = "content://";
    
    public static final String FILE = "file://";
    
    public static final String URI = "uri://";
    
    private String mProtocol;
    
    private String mHost;
    
    private int mPort;
    
    private String mPath;
    
    private Map<String, Object> mParams;
    
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
    
    public URL(String protocol, String host, int port, String path) {
        mProtocol = protocol;
        mHost = host;
        mPort = port;
        mPath = path;
        
        mParams = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
    }
    
    public <T> void addParameter(String key, T value) {
        mParams.put(key, value);
    }
    
    @Override
    public String toString() {
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
        
        sb.append("/");
        
        if (!TextUtils.isEmpty(mPath)) {
            sb.append(mPath);
        }
        
        boolean first = false;
        for (Entry<String, Object> entry : mParams.entrySet()) {
            sb.append(String.format(first ? "?%s=%s" : "&%s=%s", entry.getKey(), entry.getValue()));
        }
        
        return sb.toString();
    }
    
    public static void main(String[] args) {
        URL url = new URL(host)
    }
}
