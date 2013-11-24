package com.pplive.liveplatform.core.rest;

public class PageListResp<T> extends Resp<PageList<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
