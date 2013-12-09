package com.pplive.liveplatform.core.service.comment;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.comment.auth.PBarTokenAuthentication;
import com.pplive.liveplatform.core.service.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.service.comment.resp.FeedDetailListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class PbarService {

    private static final String TAG = PbarService.class.getSimpleName();
    
    private static final PbarService sInstance = new PbarService();

    private static final String TEMPLATE_GET_REFS = new BaseURL(Protocol.HTTP, Constants.SC_API_HOST, "/sc/v2/live/ref/{refId}/feed?pagesize={pagesize}").toString();
    
    public static PbarService getInstance() {
        return sInstance;
    }
   
    private PbarService() {
        
    }
    
    public FeedDetailList getFeeds(String coToken, long pid) {
        return getFeeds(coToken, pid, 30 /* pagesize */);
    }
    
    public FeedDetailList getFeeds(String coToken, long pid, int pagesize) {
        Log.d(TAG, "pid: " + pid + "; pagesize: " + pagesize);
        
        PBarTokenAuthentication authentication = new PBarTokenAuthentication(coToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAuthorization(authentication);
        
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new GsonHttpMessageConverter());
        
        HttpEntity<String> req = new HttpEntity<String>(headers);
        
        HttpEntity<FeedDetailListResp> rep = template.exchange(TEMPLATE_GET_REFS, HttpMethod.GET, req, FeedDetailListResp.class, "LivePlatform-pbar_" + pid, pagesize);
        
        return rep.getBody().getData();
    }
}
