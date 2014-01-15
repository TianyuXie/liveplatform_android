package com.pplive.liveplatform.core.task;

import com.pplive.liveplatform.util.StringUtil;

public class TaskCancelEvent {
    private String message;

    public TaskCancelEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return StringUtil.safeString(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
