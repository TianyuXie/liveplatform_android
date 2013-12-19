package com.pplive.liveplatform.core.service.live;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.auth.PlayTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.Push;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.service.live.resp.PushResp;
import com.pplive.liveplatform.core.service.live.resp.WatchListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class MediaService extends RestService {

    private static final String TAG = MediaService.class.getSimpleName();

    private static final String TEMPLATE_GET_PUSH = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/publish")
            .toString();

    private static final String TEMPLATE_GET_PLAY = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/watch")
            .toString();

    private static final String TEMPLATE_GET_PREVIEW = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/preview")
            .toString();

    private static final MediaService sInstance = new MediaService();

    public static final MediaService getInstance() {
        return sInstance;
    }

    private MediaService() {

    }

    public Push getPush(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getLiveToken(coToken, pid, username);

        return getPushByLiveToken(pid, token);
    }

    public Push getPushByLiveToken(long pid, String liveToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + liveToken);

        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(liveToken));

        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        PushResp resp = null;
        try {
            HttpEntity<PushResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PUSH, HttpMethod.GET, req, PushResp.class, pid);
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

    public List<Watch> getPlayWatchList(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "coToken:" + coToken + "; pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getPlayToken(coToken, pid, username);

        return getPlayWatchListByPlayToken(pid, token);
    }

    public List<Watch> getPlayWatchListByPlayToken(long pid, String playToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + playToken);

        mRequestHeaders.setAuthorization(new PlayTokenAuthentication(playToken));

        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        WatchListResp resp = null;
        try {
            HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PLAY, HttpMethod.GET, req, WatchListResp.class, pid);
            resp = rep.getBody();
            
            if (0 == resp.getError()) {
                return resp.getList();
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

    public List<Watch> getPreviewWatchList(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getLiveToken(coToken, pid, username);

        return getPreviewWatchListByLiveToken(pid, token);
    }

    public List<Watch> getPreviewWatchListByLiveToken(long pid, String token) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + token);

        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(token));

        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        WatchListResp resp = null;
        try {
            HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PREVIEW, HttpMethod.GET, req, WatchListResp.class, pid);
            resp = rep.getBody();
            
            if (null != resp) {
                return resp.getList();
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
