package com.pplive.liveplatform.core.service.live.model;

public class Subject {

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
