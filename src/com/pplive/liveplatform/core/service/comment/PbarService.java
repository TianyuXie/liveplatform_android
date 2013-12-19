package com.pplive.liveplatform.core.service.comment;

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
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.comment.auth.PBarTokenAuthentication;
import com.pplive.liveplatform.core.service.comment.model.Feed;
import com.pplive.liveplatform.core.service.comment.model.Feed.Type;
import com.pplive.liveplatform.core.service.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.service.comment.resp.FeedDetailListResp;
import com.pplive.liveplatform.core.service.comment.resp.FeedIdResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class PbarService {

    private static final String TAG = PbarService.class.getSimpleName();

    private static final PbarService sInstance = new PbarService();

    private static final String TEMPLATE_GET_FEEDS = new BaseURL(Protocol.HTTP, Constants.SC_API_HOST, "/sc/v2/live/ref/{refId}/feed?pagesize={pagesize}")
            .toString();

    private static final String TEMPLATE_PUT_FEED = new BaseURL(Protocol.HTTP, Constants.SC_API_HOST, "/sc/v2/live/feed/info").toString();

    public static PbarService getInstance() {
        return sInstance;
    }

    private PbarService() {

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

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpEntity<String> req = new HttpEntity<String>(headers);

        FeedDetailListResp resp = null;
        try {
            HttpEntity<FeedDetailListResp> rep = template.exchange(TEMPLATE_GET_FEEDS, HttpMethod.GET, req, FeedDetailListResp.class, "LivePlatform-pbar_" + pid,
                    pagesize);
            
            resp = rep.getBody();
            
            return resp.getData();
        } catch (Exception e) {
            if (null != resp) {
                throw new LiveHttpException(resp.getError());
            }
        }

        throw new LiveHttpException();
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

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpEntity<Feed> req = new HttpEntity<Feed>(feed, headers);

        FeedIdResp resp = null;
        try {
            HttpEntity<FeedIdResp> rep = template.exchange(TEMPLATE_PUT_FEED, HttpMethod.PUT, req, FeedIdResp.class);
    
            resp = rep.getBody();
    
            return resp.getData();
        } catch (Exception e) {
            if (null != resp) {
                throw new LiveHttpException(resp.getError());
            }
        }
        
        throw new LiveHttpException();
    }
}
