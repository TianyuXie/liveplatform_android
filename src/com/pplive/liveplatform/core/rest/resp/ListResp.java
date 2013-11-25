package com.pplive.liveplatform.core.rest.resp;

import com.pplive.liveplatform.core.rest.model.List;

public class ListResp<T> extends DataResp<List<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
