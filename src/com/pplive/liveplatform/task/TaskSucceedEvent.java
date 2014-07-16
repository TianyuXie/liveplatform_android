package com.pplive.liveplatform.task;

public class TaskSucceedEvent {
    private TaskContext context;

    public TaskSucceedEvent(TaskContext context) {
        this.context = context;
    }

    public TaskContext getContext() {
        return context;
    }

    public void setContext(TaskContext context) {
        this.context = context;
    }
}
