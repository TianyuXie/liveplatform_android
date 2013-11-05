package com.pplive.liveplatform.core.task;

public class TaskFailedEvent {
    private String message;

    public TaskFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
