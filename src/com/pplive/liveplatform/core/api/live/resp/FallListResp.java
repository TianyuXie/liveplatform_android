package com.pplive.liveplatform.core.api.live.resp;

import com.pplive.liveplatform.core.api.live.model.FallList;

public class FallListResp<T> extends Resp<FallList<T>> {

    public FallList<T> getFallList() {
        return getData();
    }
}
