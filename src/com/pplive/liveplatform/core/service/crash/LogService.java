package com.pplive.liveplatform.core.service.crash;

import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.GsonHttpMessageConverterEx;
import com.pplive.liveplatform.core.service.RestTemplateFactory;
import com.pplive.liveplatform.core.service.crash.resp.CrashResp;
import com.pplive.liveplatform.util.URL;
import com.pplive.liveplatform.util.URL.Protocol;

public class LogService {

    static final String TAG = LogService.class.getSimpleName();

    private static final String TEMPLATE_CRASH_UPLOAD = new URL(Protocol.HTTP, Constants.CRASH_UPLOAD_SERVICE_HOST,
            "/up.do?ver=1&platform=ibo_aph_crash&deviceid={deviceid}&devicetype={devicetype}&osv={osv}&sv={sv}&channel={channel}&issave=1").toString();

    private static final LogService sInstance = new LogService();

    public static LogService getInstance() {
        return sInstance;
    }

    private RestTemplate mRestTemplate;

    private LogService() {
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
