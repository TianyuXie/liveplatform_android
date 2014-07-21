package com.pplive.liveplatform.core.api.live.model;

import java.io.Serializable;

public class Tag implements Serializable {

    private static final long serialVersionUID = 1914668791031361241L;

    String tagname;

    int tagcount;

    public Tag(String tag) {
        tagname = tag;
    }

    public String getTagName() {
        return tagname;
    }

    public int getTagCount() {
        return tagcount;
    }
}
