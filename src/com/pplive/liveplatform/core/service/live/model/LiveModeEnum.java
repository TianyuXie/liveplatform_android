package com.pplive.liveplatform.core.service.live.model;

import com.google.gson.annotations.SerializedName;

public enum LiveModeEnum {

    @SerializedName("camera")
    CAMERA,

    @SerializedName("xsplit")
    XSPLIT,

    @SerializedName("rtmp")
    RTMP,

    @SerializedName("unknown")
    UNKNOWN;

}
