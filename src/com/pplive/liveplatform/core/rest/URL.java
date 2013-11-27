package com.pplive.liveplatform.core.rest;


import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.net.Uri;
import android.text.TextUtils;

public class URL implements Serializable, Cloneable {

    private static final long serialVersionUID = -3649683692013310693L;
    
    private String mBaseUrl;
    
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
    
    public URL(Protocol schema, String host, String path) {
        this(schema, host, -1, path);
    }
    
    public URL(Protocol schema, String host, int port, String path) {
        mParams = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
        
        StringBuilder sb = new StringBuilder(); 
        
        if (null != schema) {
            sb.append(schema.toString());
        }
        
        if (!TextUtils.isEmpty(host)) {
            sb.append(host);
        }
        
        if (port > 0) {
            sb.append(String.format(":%d", port));
        }
        
        if (!TextUtils.isEmpty(path)) {
            sb.append(path);
        }
        
        setBaseUrl(sb.toString());
    }
    
    public final void setBaseUrl(String url) {
        mBaseUrl = url;
    }
    
    public final <T> void addParameter(String key, T value) {
        mParams.put(key, value);
    }
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(mBaseUrl); 
        
        boolean first = true;
        for (Entry<String, Object> entry : mParams.entrySet()) {
            sb.append(String.format(first ? "?%s=%s" : "&%s=%s", entry.getKey(), entry.getValue()));
            first = true;
        }
        
        return sb.toString();
    }
    
    public final Uri toUri() {
        return Uri.parse(this.toString());
    }
    
}
