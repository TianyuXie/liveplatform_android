package com.pplive.liveplatform.core.service.live.resp;

import com.pplive.liveplatform.core.service.live.model.FallList;

public class FallListResp<T> extends Resp<FallList<T>> {

    public FallList<T> getFallList() {
        return getData();
    }
}
