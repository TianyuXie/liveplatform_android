package com.pplive.liveplatform.core.api;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;


public class GsonHttpMessageConverterEx extends GsonHttpMessageConverter {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {

        return true;
    }
}
