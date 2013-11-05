package com.pplive.liveplatform.core.task;

public class TaskTimeoutEvent {
    private String message;

    public TaskTimeoutEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
