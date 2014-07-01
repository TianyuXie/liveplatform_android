package com.pplive.liveplatform.core.api.crash;

import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.GsonHttpMessageConverterEx;
import com.pplive.liveplatform.core.api.RestTemplateFactory;
import com.pplive.liveplatform.core.api.crash.resp.CrashResp;
import com.pplive.liveplatform.util.URL;
import com.pplive.liveplatform.util.URL.Protocol;

public class LogAPI {

    static final String TAG = LogAPI.class.getSimpleName();

    private static final String TEMPLATE_CRASH_UPLOAD = new URL(Protocol.HTTP, Constants.CRASH_UPLOAD_SERVICE_HOST,
            "/up.do?ver=1&platform=ibo_aph_crash&deviceid={deviceid}&devicetype={devicetype}&osv={osv}&sv={sv}&channel={channel}&issave=1").toString();

    private static final LogAPI sInstance = new LogAPI();

    public static LogAPI getInstance() {
        return sInstance;
    }

    private RestTemplate mRestTemplate;

    private LogAPI() {
        mRestTemplate = RestTemplateFactory.newInstance();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverterEx());
    }

    public String uploadCrash(String deviceId, String deviceType, String osVersion, String version, String channel, boolean issave) {
        return uploadCrash(deviceId, deviceType, osVersion, version, channel, issave ? 1 : 0);
    }

    private String uploadCrash(String deviceId, String deviceType, String osVersion, String version, String channel, int issave) {

        CrashResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_CRASH_UPLOAD, CrashResp.class, deviceId, deviceType, osVersion, version, channel, issave);

        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp && resp.uploadDump()) {
            return resp.getUploadUrl();
        }

        return null;
    }
}
