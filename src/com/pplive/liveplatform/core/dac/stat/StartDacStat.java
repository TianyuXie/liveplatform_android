package com.pplive.liveplatform.core.dac.stat;

import com.pplive.liveplatform.core.dac.data.StartData;
import com.pplive.liveplatform.core.dac.info.AppInfo;

public class StartDacStat extends BaseDacStat implements StartData {
    
    private static final long serialVersionUID = -8656362193060837915L;

    public StartDacStat() {
        addMetaItem(KEY_LOG_KIND, LOG_KIND_START);
        addValueItem(KEY_IS_FIRST, IS_FIRST_FALSE);
        addValueItem(KEY_INSTALL_SOURCE, AppInfo.getChannel());
    }
    
    public void setIsFirstStart(boolean isFirst) {
        addValueItem(KEY_IS_FIRST, isFirst ? IS_FIRST_TRUE : IS_FIRST_FALSE);
    }
    
}
