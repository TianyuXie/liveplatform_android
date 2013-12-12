package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveAlive;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.resp.LiveAliveResp;
import com.pplive.liveplatform.core.service.live.resp.Resp;
import com.pplive.liveplatform.util.URL.Protocol;

public class LiveControlService extends RestService {

    private static final String TAG = LiveControlService.class.getSimpleName();

    private static final String TEMPLATE_UPDATE_LIVE_STATUS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/c/v1/pptv/program/{pid}/livestatus")
            .toString();

    private static final String TEMPLATE_KEEP_LIVE_ALIVE = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/c/v1/pptv/program/{pid}/livealive")
            .toString();

    private static final LiveControlService sInstance = new LiveControlService();

    public static final LiveControlService getInstance() {
        return sInstance;
    }

    private LiveControlService() {
    }

    public void updateLiveStatusByCoToken(String coToken, long pid, LiveStatusEnum livestatus, String username) {
        String liveToken = TokenService.getInstance().getLiveToken(coToken, pid, username);

        updateLiveStatusByLiveToken(liveToken, pid, livestatus);
    }

    public void updateLiveStatusByLiveToken(String liveToken, long pid, LiveStatusEnum livestatus) {
        Log.d(TAG, "pid: " + pid + "; livestatus: " + livestatus);

        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(liveToken));
        HttpEntity<?> req = new HttpEntity<LiveStatus>(new LiveStatus(livestatus), mRequestHeaders);

        mRestTemplate.postForObject(TEMPLATE_UPDATE_LIVE_STATUS, req, Resp.class, pid);
    }

    public LiveAlive keepLiveAlive(String coToken, long pid) {
        Log.d(TAG, "pid: ");
        
        mRequestHeaders.setAuthorization(new UserTokenAuthentication(coToken));
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);
        
        HttpEntity<LiveAliveResp> rep = mRestTemplate.exchange(TEMPLATE_KEEP_LIVE_ALIVE, HttpMethod.GET, req, LiveAliveResp.class, pid);
        
        LiveAliveResp body = rep.getBody();
        
        return body.getData();
    }
}
