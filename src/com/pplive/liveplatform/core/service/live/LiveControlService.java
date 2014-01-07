package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.os.AsyncTask;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveAlive;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.LiveAliveResp;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
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
    
    public void updateLiveStatusByCoTokenAsync(final String coToken, final Program program, final LiveStatusEnum livestatus, final String username) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            
            @Override
            protected Boolean doInBackground(Void... params) {
                
                try {
                    
                    return updateLiveStatusByCoToken(coToken, program, livestatus, username);
                } catch (LiveHttpException e) {
                    
                }
                
                return false;
            }
        };
        
        task.execute();
    }
    
    public boolean updateLiveStatusByCoToken(String coToken, Program program, LiveStatusEnum livestatus, String username) throws LiveHttpException {
        String liveToken = TokenService.getInstance().getLiveToken(coToken, program.getId(), username);

        return updateLiveStatusByLiveToken(liveToken, program, livestatus);
    }
    
    public boolean updateLiveStatusByLiveToken(String liveToken, Program program, LiveStatusEnum livestatus) throws LiveHttpException {
        Log.d(TAG, "pid: " + program.getId() + "; livestatus: " + livestatus);
        
        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(liveToken));
        HttpEntity<?> req = new HttpEntity<LiveStatus>(new LiveStatus(livestatus), mRequestHeaders);
        
        MessageResp resp = null;
        try {
    
            resp = mRestTemplate.postForObject(TEMPLATE_UPDATE_LIVE_STATUS, req, MessageResp.class, program.getId());
            
            if (0 == resp.getError()) {
                program.setLiveStatus(livestatus);
                return true;
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
        
        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public LiveAlive keepLiveAlive(String coToken, long pid) throws LiveHttpException {
        Log.d(TAG, "pid: ");
        
        mRequestHeaders.setAuthorization(new UserTokenAuthentication(coToken));
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);
        
        LiveAliveResp resp = null; 
        try {
            
            HttpEntity<LiveAliveResp> rep = mRestTemplate.exchange(TEMPLATE_KEEP_LIVE_ALIVE, HttpMethod.GET, req, LiveAliveResp.class, pid);
           
            resp = rep.getBody();
            
            if (0 == resp.getError()) {
                return resp.getData();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
        
        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }
}
