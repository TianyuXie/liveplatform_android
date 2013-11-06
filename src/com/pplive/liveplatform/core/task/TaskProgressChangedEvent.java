package com.pplive.liveplatform.core.task;

public class TaskProgressChangedEvent {

    private int progressPercentage = -1;

    public TaskProgressChangedEvent(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
