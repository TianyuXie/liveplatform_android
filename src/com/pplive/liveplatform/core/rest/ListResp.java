package com.pplive.liveplatform.core.rest;

public class ListResp<T> extends Resp<List<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
