package com.pplive.liveplatform.core.task;

public class TaskResult {

    public final static TaskResult FINISHED = new TaskResult(TaskStatus.Finished);

    private TaskStatus status;
    private TaskContext context;
    private String message;

    public TaskResult(TaskStatus status) {
        this(null, status);
    }

    public TaskResult(TaskContext context, TaskStatus status) {
        this(context, status, "");
    }

    public TaskResult(TaskStatus status, String message) {
        this(null, status, message);
    }

    public TaskResult(TaskContext context, TaskStatus status, String message) {
        this.status = status;
        this.context = context;
        this.message = message;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskContext getContext() {
        return context;
    }

    public void setContext(TaskContext context) {
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
