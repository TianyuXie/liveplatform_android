package com.pplive.liveplatform.core.task;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class Task extends AsyncTask<TaskContext, Integer, TaskResult> {

    public abstract String getID();

    public abstract String getName();

    public abstract void cancel();

    public abstract void pause();

    public abstract void resume();

    private boolean isReturn = false;

    protected boolean isCancel = false;

    private static final int DEFAULT_TIME_OUT = 20 * 1000;

    private Timer timer = new Timer();

    private ArrayList<OnTaskFinishedListener> taskFinishedListeners = new ArrayList<OnTaskFinishedListener>();

    private ArrayList<OnTaskFailedListener> taskFailedListeners = new ArrayList<OnTaskFailedListener>();

    private ArrayList<OnTaskProgressChangedListener> taskProgressChangedListeners = new ArrayList<OnTaskProgressChangedListener>();

    private ArrayList<OnTaskTimeoutListener> taskTimeoutListeners = new ArrayList<OnTaskTimeoutListener>();

    private ArrayList<OnTaskCancelListner> taskCancelListeners = new ArrayList<Task.OnTaskCancelListner>();

    private int timeout = DEFAULT_TIME_OUT;

    public Task(int timeout) {
        this.timeout = timeout;
    }

    public Task() {

    }

    @Override
    protected void onPreExecute() {
        // Timer, use to count the time, the default time is 10 seconds
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
                onTaskTimeout(Task.this, new TaskTimeoutEvent("Task timeout"));
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
            onTaskFailed(this, new TaskFailedEvent(result.getMessage()));
        }
    }

    public void addTaskFinishedListener(OnTaskFinishedListener listener) {
        taskFinishedListeners.add(listener);
    }

    public void removeTaskFinishedListener(OnTaskFinishedListener listener) {
        taskFinishedListeners.remove(listener);
    }

    public void addTaskFailedListener(OnTaskFailedListener listener) {
        taskFailedListeners.add(listener);
    }

    public void removeTaskFailedListener(OnTaskFailedListener listener) {
        taskFailedListeners.remove(listener);
    }

    public void addTaskProgressChangeListener(OnTaskProgressChangedListener listener) {
        taskProgressChangedListeners.add(listener);
    }

    public void removeTaskProgressChangeListener(OnTaskProgressChangedListener listener) {
        taskProgressChangedListeners.remove(listener);
    }

    public void addTaskTimeoutListener(OnTaskTimeoutListener listener) {
        taskTimeoutListeners.add(listener);
    }

    public void removeTaskTimeoutListener(OnTaskTimeoutListener listener) {
        taskTimeoutListeners.remove(listener);
    }

    public void addTaskCancelListener(OnTaskCancelListner listener) {
        taskCancelListeners.add(listener);
    }

    public void removeTaskCancelListener(OnTaskCancelListner listener) {
        taskCancelListeners.remove(listener);
    }

    protected void onTaskFinished(Object sender, TaskFinishedEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskFinishedListener listener : taskFinishedListeners) {
                listener.onTaskFinished(sender, event);
            }
        }
    }

    protected void onTaskFailed(Object sender, TaskFailedEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskFailedListener listener : taskFailedListeners) {
                listener.onTaskFailed(sender, event);
            }
        }
    }

    protected void onTaskProgressChanged(Object sender, TaskProgressChangedEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskProgressChangedListener listener : taskProgressChangedListeners) {
                listener.onProgressChanged(sender, event);
            }
        }
    }

    protected void onTaskTimeout(Object sender, TaskTimeoutEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskTimeoutListener listener : taskTimeoutListeners) {
                listener.onTimeout(sender, event);
            }
        }
    }

    protected void onTaskCancel(Object sender, TaskCancelEvent event) {
        if (!isReturn) {
            isReturn = true;
            for (OnTaskCancelListner listener : taskCancelListeners) {
                listener.onTaskCancel(sender, event);
            }
        }
    }

    public interface OnTaskFinishedListener {
        public abstract void onTaskFinished(Object sender, TaskFinishedEvent event);
    }

    public interface OnTaskFailedListener {
        public abstract void onTaskFailed(Object sender, TaskFailedEvent event);
    }

    public interface OnTaskProgressChangedListener {
        public abstract void onProgressChanged(Object sender, TaskProgressChangedEvent event);
    }

    public interface OnTaskTimeoutListener {
        public abstract void onTimeout(Object sender, TaskTimeoutEvent event);
    }

    public interface OnTaskCancelListner {
        public abstract void onTaskCancel(Object sender, TaskCancelEvent event);
    }
}
