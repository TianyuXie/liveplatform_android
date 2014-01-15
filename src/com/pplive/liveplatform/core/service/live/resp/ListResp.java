package com.pplive.liveplatform.core.service.live.resp;

import com.pplive.liveplatform.core.service.live.model.List;

public class ListResp<T> extends Resp<List<T>> {

    public java.util.List<T> getList() {
        return data.getList();
    }
}
