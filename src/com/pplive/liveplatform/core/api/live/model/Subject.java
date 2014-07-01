package com.pplive.liveplatform.core.api.live.model;

import java.io.Serializable;

public class Subject implements Serializable {

    private static final long serialVersionUID = 8851682563347303969L;

    String subject;

    int seq;

    String image;

    int subject_id;

    int hasvod;

    public String getSubjectName() {
        return subject;
    }

    public int getSeq() {
        return seq;
    }

    public String getImageUrl() {
        return image;
    }

    public int getId() {
        return subject_id;
    }

    public boolean hasVod() {
        return 1 == hasvod;
    }

}
