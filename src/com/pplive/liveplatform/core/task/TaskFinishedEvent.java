package com.pplive.liveplatform.core.task;

public class TaskFinishedEvent {
    private TaskContext context;

    public TaskFinishedEvent(TaskContext context) {
        this.context = context;
    }

    public TaskContext getContext() {
        return context;
    }

    public void setContext(TaskContext context) {
        this.context = context;
    }
}
