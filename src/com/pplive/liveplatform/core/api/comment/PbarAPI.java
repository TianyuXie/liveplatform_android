package com.pplive.liveplatform.core.api.comment;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.BaseURL;
import com.pplive.liveplatform.core.api.RestTemplateFactory;
import com.pplive.liveplatform.core.api.comment.auth.PBarTokenAuthentication;
import com.pplive.liveplatform.core.api.comment.model.Feed;
import com.pplive.liveplatform.core.api.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.api.comment.model.Feed.Type;
import com.pplive.liveplatform.core.api.comment.resp.FeedDetailListResp;
import com.pplive.liveplatform.core.api.comment.resp.FeedIdResp;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.util.URL.Protocol;

public class PbarAPI {

    private static final String TAG = PbarAPI.class.getSimpleName();

    private static final PbarAPI sInstance = new PbarAPI();

    private static final String TEMPLATE_GET_FEEDS = new BaseURL(Protocol.HTTP, Constants.SC_API_HOST, "/sc/v2/live/ref/{refId}/feed?pagesize={pagesize}")
            .toString();

    private static final String TEMPLATE_PUT_FEED = new BaseURL(Protocol.HTTP, Constants.SC_API_HOST, "/sc/v2/live/feed/info").toString();

    public static PbarAPI getInstance() {
        return sInstance;
    }
    
    private RestTemplate mRestTemplate;

    private PbarAPI() {
        mRestTemplate = RestTemplateFactory.newInstance();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
    }

    public FeedDetailList getFeeds(String coToken, long pid) throws LiveHttpException {
        return getFeeds(coToken, pid, 30 /* pagesize */);
    }

    public FeedDetailList getFeeds(String coToken, long pid, int pagesize) throws LiveHttpException {
        
        Log.d(TAG, "pid: " + pid + "; pagesize: " + pagesize);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (!TextUtils.isEmpty(coToken)) {
            PBarTokenAuthentication authentication = new PBarTokenAuthentication(coToken);
            headers.setAuthorization(authentication);
        }

        HttpEntity<String> req = new HttpEntity<String>(headers);

        FeedDetailListResp resp = null;
        try {
            HttpEntity<FeedDetailListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_FEEDS, HttpMethod.GET, req, FeedDetailListResp.class, "LivePlatform-pbar_" + pid,
                    pagesize);
            
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

    public long putFeed(String coToken, long pid, String content) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; content: " + content);

        Feed feed = new Feed(pid, content, Type.COMMENT);

        return putFeed(coToken, feed);
    }

    public long putFeed(String coToken, long pid, String content, Feed.Type type) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; content: " + content + "; type: " + type);

        Feed feed = new Feed(pid, content, type);

        return putFeed(coToken, feed);
    }

    public long putFeed(String coToken, Feed feed) throws LiveHttpException {
        Log.d(TAG, "putFeed");

        PBarTokenAuthentication authentication = new PBarTokenAuthentication(coToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAuthorization(authentication);

        HttpEntity<Feed> req = new HttpEntity<Feed>(feed, headers);

        FeedIdResp resp = null;
        try {
            HttpEntity<FeedIdResp> rep = mRestTemplate.exchange(TEMPLATE_PUT_FEED, HttpMethod.PUT, req, FeedIdResp.class);
    
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
