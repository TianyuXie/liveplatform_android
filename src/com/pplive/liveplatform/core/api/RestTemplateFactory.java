package com.pplive.liveplatform.core.api;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory {
    
    private static final HttpComponentsClientHttpRequestFactory HTTP_REQUEST_FACTORY = new OptimizedHttpComponentsClientHttpRequestFactory(); 

    public static RestTemplate newInstance() {
        return new RestTemplate(false, HTTP_REQUEST_FACTORY);
    }
}
