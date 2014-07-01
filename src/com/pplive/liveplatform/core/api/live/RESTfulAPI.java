package com.pplive.liveplatform.core.api.live;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.pplive.liveplatform.core.api.RestTemplateFactory;

public abstract class RESTfulAPI {

    protected RestTemplate mRestTemplate;
    
    protected HttpHeaders mHttpHeaders;
    
    protected RESTfulAPI() {
        mRestTemplate = RestTemplateFactory.newInstance();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
}
