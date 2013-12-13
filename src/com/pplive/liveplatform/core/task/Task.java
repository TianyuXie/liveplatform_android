package com.pplive.liveplatform.core.task;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public abstract class Task extends AsyncTask<TaskContext, Integer, TaskResult> {
    public final static String KEY_TOKEN = "token";
    public final static String KEY_PID = "pid";
    public final static String KEY_USERNAME = "username";

    private static final int DEFAULT_TIME_OUT = 20 * 1000;

    private boolean isReturn;

    private int timeout;

    private Timer timer = new Timer();

    private ArrayList<OnTaskListener> taskListeners = new ArrayList<OnTaskListener>();

    private TaskContext backContext;

    public Task(int timeout) {
        this.timeout = timeout;
    }

    public Task() {
        this.timeout = DEFAULT_TIME_OUT;
    }

    public abstract String getID();

    public abstract String getName();

    public abstract void pause();

    public abstract void resume();

    @Override
    protected void onPreExecute() {
        // Timer, use to count the time, the default time is 20 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, timeout);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                onTaskTimeout(Task.this, new TaskTimeoutEvent("Task timeout", backContext));
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onPostExecute(TaskResult result) {
        if (isReturn)
            return;
        if (result == null) {
            result = new TaskResult(TaskStatus.Failed, "Can not return null when the task done");
        }
        if (result.getStatus() == TaskStatus.Finished) {
            onTaskFinished(this, new TaskFinishedEvent(result.getContext()));
        } else if (result.getStatus() == TaskStatus.Cancel) {
            onTaskCancel(this, new TaskCancelEvent(result.getMessage()));
        } else {
            onTaskFailed(this, new TaskFailedEvent(result.getMessage(), result.getContext()));
        }
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setBackContext(TaskContext backContext) {
        this.backContext = backContext;
    }

    @Override
    protected void onCancelled() {
        if (isReturn)
            return;
        onTaskCancel(this, new TaskCancelEvent("Canceled"));
    }

    public void addTaskListener(OnTaskListener listener) {
        taskListeners.add(listener);
    }

    public void removeTaskCancelListener(OnTaskListener listener) {
        taskListeners.remove(listener);
    }

    protected void onTaskFinished(Object sender, TaskFinishedEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskListener listener : taskListeners) {
                listener.onTaskFinished(sender, event);
            }
        }
    }

    protected void onTaskFailed(Object sender, TaskFailedEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskListener listener : taskListeners) {
                listener.onTaskFailed(sender, event);
            }
        }
    }

    protected void onTaskProgressChanged(Object sender, TaskProgressChangedEvent event) {
        if (!isReturn) {
            for (OnTaskListener listener : taskListeners) {
                listener.onProgressChanged(sender, event);
            }
        }
    }

    protected void onTaskTimeout(Object sender, TaskTimeoutEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskListener listener : taskListeners) {
                listener.onTimeout(sender, event);
            }
        }
    }

    protected void onTaskCancel(Object sender, TaskCancelEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskListener listener : taskListeners) {
                listener.onTaskCancel(sender, event);
            }
        }
    }

    public interface OnTaskListener {
        void onTaskFinished(Object sender, TaskFinishedEvent event);

        void onTaskFailed(Object sender, TaskFailedEvent event);

        void onProgressChanged(Object sender, TaskProgressChangedEvent event);

        void onTimeout(Object sender, TaskTimeoutEvent event);

        void onTaskCancel(Object sender, TaskCancelEvent event);
    }
}
