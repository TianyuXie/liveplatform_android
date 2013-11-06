package com.pplive.liveplatform.core.task;

public class TaskCancelEvent {
    private String message;

    public TaskCancelEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
