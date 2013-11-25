package com.pplive.liveplatform.core.rest.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.http.LiveTokenAuthentication;
import com.pplive.liveplatform.core.rest.http.PlayTokenAuthentication;
import com.pplive.liveplatform.core.rest.http.Url;
import com.pplive.liveplatform.core.rest.model.Push;
import com.pplive.liveplatform.core.rest.model.Watch;
import com.pplive.liveplatform.core.rest.resp.PushResp;
import com.pplive.liveplatform.core.rest.resp.WatchListResp;

public class MediaService extends AbsService {

    private static final String TAG = MediaService.class.getSimpleName();

    private static final String TEMPLATE_GET_PUSH = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/media/v1/pptv/program/{pid}/publish")
            .toString();

    private static final String TEMPLATE_GET_PLAY = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/media/v1/pptv/program/{pid}/watch")
            .toString();

    private static final String TEMPLATE_GET_PREVIEW = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT,
            "/media/v1/pptv/program/{pid}/preview").toString();

    private static final MediaService sInstance = new MediaService();

    public static final MediaService getInstance() {
        return sInstance;
    }

    private MediaService() {

    }

    public Push getPush(long pid, String token) {
        Log.d(TAG, "pid: " + pid + "; token: " + token);
        
        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(token));

        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        HttpEntity<PushResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PUSH, HttpMethod.GET, req, PushResp.class, pid);
        PushResp body = rep.getBody();

        return body.getData();
    }

    public List<Watch> getPlayWatchList(long pid, String token) {
        Log.d(TAG, "pid: " + pid + "; token: " + token);
        
        mRequestHeaders.setAuthorization(new PlayTokenAuthentication(token));

        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PLAY, HttpMethod.GET, req, WatchListResp.class, pid);
        WatchListResp body = rep.getBody();

        return body.getList();
    }

    public List<Watch> getPreviewWatchList(long pid, String token) {
        Log.d(TAG, "pid: " + pid + "; token: " + token);
        
        mRequestHeaders.setAuthorization(new LiveTokenAuthentication(token));

        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PREVIEW, HttpMethod.GET, req, WatchListResp.class, pid);
        WatchListResp body = rep.getBody();

        return body.getList();
    }
}
