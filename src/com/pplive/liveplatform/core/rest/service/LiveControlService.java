package com.pplive.liveplatform.core.rest.service;

import org.springframework.http.HttpEntity;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.Protocol;
import com.pplive.liveplatform.core.rest.URL;
import com.pplive.liveplatform.core.rest.model.LiveStatus;
import com.pplive.liveplatform.core.rest.model.LiveStatusEnum;
import com.pplive.liveplatform.core.rest.resp.Resp;

public class LiveControlService extends AbsService {

    private static final String TAG = LiveControlService.class.getSimpleName();

    private static final String TEMPLATE_UPDATE_LIVESTATUS = new URL(Protocol.HTTP, Constants.TEST_HOST, Constants.TEST_PORT,
            "/c/v1/pptv/program/{pid}/livestatus").toString();

    private static final LiveControlService sInstance = new LiveControlService();

    public static final LiveControlService getInstance() {
        return sInstance;
    }

    private LiveControlService() {
    }

    public void updateLiveStatusById(long pid, LiveStatusEnum livestatus) {
        Log.d(TAG, "pid: " + pid + "; livestatus: " + livestatus);
        
        mRequestHeaders.setAuthorization(mCoTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<LiveStatus>(new LiveStatus(livestatus), mRequestHeaders);

        mRestTemplate.postForObject(TEMPLATE_UPDATE_LIVESTATUS, req, Resp.class, pid);
    }
}
