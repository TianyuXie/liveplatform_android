package com.pplive.liveplatform.core.api.live.model;

import com.google.gson.annotations.SerializedName;

public enum LiveModeEnum {

    @SerializedName("ugc")
    UGC,

    @SerializedName("camera")
    CAMERA,

    @SerializedName("xsplit")
    XSPLIT,

    @SerializedName("rtmp")
    RTMP,

    @SerializedName("unknown")
    UNKNOWN;

}
