package com.pplive.liveplatform.core.rest.http;


import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.net.Uri;
import android.text.TextUtils;

public class Url implements Serializable {

    private static final long serialVersionUID = -3649683692013310693L;
    
    public enum Schema {
        HTTP {
            @Override
            public String toString() {
                return "http://";
            }
        }, 
        HTTPS {
            @Override
            public String toString() {
                return "https://";
            }
        }, 
        RTMP {
            @Override
            public String toString() {
                return "rtmp://";
            }
        },
        RTSP {
            @Override
            public String toString() {
                return "rtsp://";
            }
        }, 
        CONTENT {
            @Override
            public String toString() {
                return "content://";
            }
        }, 
        FILE {
            @Override
            public String toString() {
                return "file://";
            }
        };
        
        public abstract String toString();
    }
    
    private String mBaseUrl;
    
    private final Map<String, Object> mParams;
    
    public Url() {
        this(null);
    }
    
    public Url(String host) {
        this(host, null);
    }
    
    public Url(String host, String path) {
        this(host, -1, path);
    }
    
    public Url(String host, int port, String path) {
        this(null, host, port, path);
    }
    
    public Url(Schema schema, String host, String path) {
        this(schema, host, -1, path);
    }
    
    public Url(Schema schema, String host, int port, String path) {
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
        
        boolean first = false;
        for (Entry<String, Object> entry : mParams.entrySet()) {
            sb.append(String.format(first ? "?%s=%s" : "&%s=%s", entry.getKey(), entry.getValue()));
        }
        
        return sb.toString();
    }
    
    public final Uri toUri() {
        return Uri.parse(this.toString());
    }
    
}
