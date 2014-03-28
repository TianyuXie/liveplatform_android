package com.pplive.liveplatform.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.net.Uri;
import android.text.TextUtils;

public class URL implements Serializable {

    static final String TAG = URL.class.getSimpleName();

    private static final long serialVersionUID = -3649683692013310693L;

    public enum Protocol {
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
        },
        PPLIVE2 {
            @Override
            public String toString() {
                return "pplive2://";
            }
        };

        public abstract String toString();
    }

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

    public URL(Protocol protocol, String host, String path) {
        this(protocol, host, -1, path);
    }

    public URL(Protocol protocol, String host, int port, String path) {
        mParams = Collections.synchronizedMap(new LinkedHashMap<String, Object>());

        StringBuilder sb = new StringBuilder();

        if (null != protocol) {
            sb.append(protocol.toString());
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

        if (mParams.size() > 0) {
            sb.append(mBaseUrl.contains("?") ? "&" : "?");

            boolean isFirst = true;
            for (Entry<String, Object> entry : mParams.entrySet()) {
                sb.append(String.format(isFirst ? "%s=%s" : "&%s=%s", entry.getKey(), entry.getValue()));
                isFirst = false;
            }
        }

        return sb.toString();
    }

    public final Uri toUri() {
        return Uri.parse(this.toString());
    }

}
