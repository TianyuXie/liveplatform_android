package com.pplive.liveplatform.core.service.live;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.auth.PlayTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.Push;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.service.live.model.WatchList;
import com.pplive.liveplatform.core.service.live.resp.PushResp;
import com.pplive.liveplatform.core.service.live.resp.WatchListResp;
import com.pplive.liveplatform.core.service.live.resp.WatchListResp2;
import com.pplive.liveplatform.util.URL.Protocol;

public class MediaService extends RestService {

    private static final String TAG = MediaService.class.getSimpleName();

    private static final String TEMPLATE_GET_PUSH = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/publish")
            .toString();

    private static final String TEMPLATE_GET_PLAY_V1 = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/watch")
            .toString();

    private static final String TEMPLATE_GET_PLAY_V2 = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v2/pptv/program/{pid}/watch")
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

        mHttpHeaders.setAuthorization(new LiveTokenAuthentication(liveToken));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

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

    public List<Watch> getPlayWatchListV1(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "coToken:" + coToken + "; pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getPlayToken(coToken, pid, username);

        return getPlayWatchListByPlayTokenV1(pid, token);
    }

    public List<Watch> getPlayWatchListByPlayTokenV1(long pid, String playToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + playToken);

        mHttpHeaders.setAuthorization(new PlayTokenAuthentication(playToken));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

        WatchListResp resp = null;
        try {
            HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PLAY_V1, HttpMethod.GET, req, WatchListResp.class, pid);
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
    
    public WatchList getPlayWatchListV2(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "coToken: " + coToken + "; pid; " + pid + "; username: " + username);
        
        String playToken = TokenService.getInstance().getPlayToken(coToken, pid, username);
        
        return getPlayWatchListByPlayTokenV2(pid, playToken);
    }
    
    public WatchList getPlayWatchListByPlayTokenV2(long pid, String playToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + playToken);
        
        mHttpHeaders.setAuthorization(new PlayTokenAuthentication(playToken));
        
        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);
        
        WatchListResp2 resp = null;
        try {
            HttpEntity<WatchListResp2> rep = mRestTemplate.exchange(TEMPLATE_GET_PLAY_V2, HttpMethod.GET, req, WatchListResp2.class, pid);
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

    public List<Watch> getPreviewWatchList(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getLiveToken(coToken, pid, username);

        return getPreviewWatchListByLiveToken(pid, token);
    }

    public List<Watch> getPreviewWatchListByLiveToken(long pid, String token) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + token);

        mHttpHeaders.setAuthorization(new LiveTokenAuthentication(token));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

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
