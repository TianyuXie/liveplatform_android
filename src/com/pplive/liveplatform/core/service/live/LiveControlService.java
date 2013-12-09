package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.URL.Protocol;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.resp.Resp;

public class LiveControlService extends RestService {

    private static final String TAG = LiveControlService.class.getSimpleName();

    private static final String TEMPLATE_UPDATE_LIVESTATUS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/c/v1/pptv/program/{pid}/livestatus")
            .toString();

    private static final LiveControlService sInstance = new LiveControlService();

    public static final LiveControlService getInstance() {
        return sInstance;
    }

    private LiveControlService() {
    }

    public void updateLiveStatusByCoToken(String coToken, long pid, LiveStatusEnum livestatus, String username) {
        String token = TokenService.getInstance().getLiveToken(coToken, pid, username);

        updateLiveStatusByLiveToken(pid, livestatus, token);
    }

    public void updateLiveStatusByLiveToken(long pid, LiveStatusEnum livestatus, String token) {
        Log.d(TAG, "pid: " + pid + "; livestatus: " + livestatus);

        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(token));
        HttpEntity<?> req = new HttpEntity<LiveStatus>(new LiveStatus(livestatus), mRequestHeaders);

        mRestTemplate.postForObject(TEMPLATE_UPDATE_LIVESTATUS, req, Resp.class, pid);
    }
}
