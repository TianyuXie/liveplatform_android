package com.pplive.liveplatform.core.task;

public class TaskProgressChangedEvent {

    private int progress = -1;

    public TaskProgressChangedEvent(int progressPercentage) {
        this.progress = progressPercentage;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progressPercentage) {
        this.progress = progressPercentage;
    }
}
