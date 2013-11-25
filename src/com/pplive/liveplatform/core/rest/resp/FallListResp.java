package com.pplive.liveplatform.core.rest.resp;

import com.pplive.liveplatform.core.rest.model.FallList;

public class FallListResp<T> extends DataResp<FallList<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
