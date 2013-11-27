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

public abstract class AbsService {

    protected RestTemplate mRestTemplate;
    
    protected HttpHeaders mRequestHeaders;
    
    protected HttpAuthentication mCoTokenAuthentication;
    
    protected AbsService() {
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        
        mCoTokenAuthentication = new TokenAuthentication(Constants.TEST_COTK); 
        
        mRequestHeaders = new HttpHeaders();
        mRequestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
}
