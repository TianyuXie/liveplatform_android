package com.pplive.liveplatform.dac.stat;

import com.pplive.liveplatform.dac.data.WatchData;

public class WatchDacStat extends MediaDacStat implements WatchData {

    private static final long serialVersionUID = -8496347648846165725L;
    
    public WatchDacStat() {
        addValueItem(KEY_WATCH_TYPE, WATCH_TYPE_UNKNOWN);
    }

    public void setWatchType(int watch_type) {
        addValueItem(KEY_WATCH_TYPE, watch_type);
    }
}
