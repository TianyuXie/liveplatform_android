package com.pplive.liveplatform.core.service.live;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.pplive.liveplatform.core.service.RestTemplateFactory;

public abstract class RestService {

    protected RestTemplate mRestTemplate;
    
    protected HttpHeaders mHttpHeaders;
    
    protected RestService() {
        mRestTemplate = RestTemplateFactory.newInstance();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
}
