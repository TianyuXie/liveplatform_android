package com.pplive.liveplatform.core.rest.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public abstract class AbsService {

    protected RestTemplate mRestTemplate;
    
    protected HttpHeaders mRequestHeaders;
    
    protected AbsService() {
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
    }
}
