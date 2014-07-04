package com.pplive.liveplatform.core.api.live.resp;

import com.pplive.liveplatform.core.api.live.model.List;

public class ListResp<T> extends Resp<List<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
