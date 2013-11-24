package com.pplive.liveplatform.core.rest.service;

import org.springframework.http.HttpEntity;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.LiveStatus;
import com.pplive.liveplatform.core.rest.LiveStatusEnum;
import com.pplive.liveplatform.core.rest.http.Url;
import com.pplive.liveplatform.core.rest.resp.Resp;

public class LiveControlService extends AbsService {

    @SuppressWarnings("unused")
    private static final String TAG = LiveControlService.class.getSimpleName();

    private static final LiveControlService sInstance = new LiveControlService();

    public static final LiveControlService getInstance() {
        return sInstance;
    }

    private static final Url UPDATE_LIVESTATUS_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/c/v1/pptv/program/{pid}/livestatus");

    private LiveControlService() {
    }
    
    public void updateLiveStatus(long pid, LiveStatusEnum livestatus) {
        HttpEntity<LiveStatus> req = new HttpEntity<LiveStatus>(new LiveStatus(livestatus), mRequestHeaders);
        
        mRestTemplate.postForObject(UPDATE_LIVESTATUS_URL.toString(), req, Resp.class, pid);
    }
}
