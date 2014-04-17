package com.pplive.liveplatform.core.service.live.model;

public class Subject {

    int subject_id;

    String subject_name;

    int image_id;

    public Subject(int subjectId, String subjectName, int imageId) {
        subject_id = subjectId;
        subject_name = subjectName;
        image_id = imageId;
    }

    public int getId() {
        return subject_id;
    }

    public String getSubjectName() {
        return subject_name;
    }

    public int getImageId() {
        return image_id;
    }
}
