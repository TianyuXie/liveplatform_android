package com.pplive.liveplatform.core.rest.resp;

import com.pplive.liveplatform.core.rest.PageList;

public class PageListResp<T> extends DataResp<PageList<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
