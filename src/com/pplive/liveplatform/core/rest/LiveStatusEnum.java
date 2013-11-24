package com.pplive.liveplatform.core.rest;

import com.google.gson.annotations.SerializedName;

public enum LiveStatusEnum {
    
    @SerializedName("notstart")
    NOT_START,
    
    @SerializedName("init")
    INIT,
    
    @SerializedName("preview")
    PREVIWE,
    
    @SerializedName("living")
    LIVING,
    
    @SerializedName("stopped")
    STOPPED,
    
    @SerializedName("deleted")
    DELETED,
    
    @SerializedName("sysdeleted")
    SYS_DELETED;

}
