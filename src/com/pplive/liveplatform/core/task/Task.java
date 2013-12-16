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

    private boolean mReturn;

    private int mTimeout;

    private Timer mTimer;

    private ArrayList<OnTaskListener> mTaskListeners;

    private TaskContext mReturnContext;

    public Task(int timeout) {
        this.mTimeout = timeout;
        this.mTimer = new Timer();
        this.mTaskListeners = new ArrayList<OnTaskListener>();
    }

    public Task() {
        this(DEFAULT_TIME_OUT);
    }

    public abstract String getID();

    public abstract String getName();

    public abstract void pause();

    public abstract void resume();

    @Override
    protected void onPreExecute() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, mTimeout);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                onTaskTimeout(Task.this, new TaskTimeoutEvent("Task timeout", mReturnContext));
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onPostExecute(TaskResult result) {
        if (mReturn)
            return;
        if (result == null) {
            result = new TaskResult(TaskStatus.Failed, "TaskResult is null");
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
        this.mTimeout = timeout;
    }

    public void setReturnContext(TaskContext context) {
        this.mReturnContext = context;
    }

    @Override
    protected void onCancelled() {
        if (mReturn)
            return;
        onTaskCancel(this, new TaskCancelEvent("Cancelled"));
    }

    public void addTaskListener(OnTaskListener listener) {
        mTaskListeners.add(listener);
    }

    public void removeTaskCancelListener(OnTaskListener listener) {
        mTaskListeners.remove(listener);
    }

    protected void onTaskFinished(Object sender, TaskFinishedEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (OnTaskListener listener : mTaskListeners) {
                listener.onTaskFinished(sender, event);
            }
        }
    }

    protected void onTaskFailed(Object sender, TaskFailedEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (OnTaskListener listener : mTaskListeners) {
                listener.onTaskFailed(sender, event);
            }
        }
    }

    protected void onTaskProgressChanged(Object sender, TaskProgressChangedEvent event) {
        if (!mReturn) {
            for (OnTaskListener listener : mTaskListeners) {
                listener.onProgressChanged(sender, event);
            }
        }
    }

    protected void onTaskTimeout(Object sender, TaskTimeoutEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (OnTaskListener listener : mTaskListeners) {
                listener.onTimeout(sender, event);
            }
        }
    }

    protected void onTaskCancel(Object sender, TaskCancelEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (OnTaskListener listener : mTaskListeners) {
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
