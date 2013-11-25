package com.pplive.liveplatform.core.rest.resp;

import com.pplive.liveplatform.core.rest.model.FallList;

public class FallListResp<T> extends Resp<FallList<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
