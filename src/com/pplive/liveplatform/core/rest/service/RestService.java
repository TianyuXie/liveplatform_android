package com.pplive.liveplatform.core.rest.service;

import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.http.TokenAuthentication;

public abstract class RestService {

    protected RestTemplate mRestTemplate;
    
    protected HttpHeaders mRequestHeaders;
    
    protected RestService() {
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        mRestTemplate.setRequestFactory(factory);
        
        mRequestHeaders = new HttpHeaders();
        mRequestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
}
