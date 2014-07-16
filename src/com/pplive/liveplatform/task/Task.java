package com.pplive.liveplatform.task;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pplive.liveplatform.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public abstract class Task extends AsyncTask<TaskContext, Integer, TaskResult> {

    private static final int DEFAULT_TIME_OUT = 20 * 1000;

    private boolean mReturn = false;

    private int mTimeout;

    protected int mDelay;

    private Timer mTimer;

    private ArrayList<TaskListener> mTaskListeners;

    private TaskContext mReturnContext;

    public Task(int timeout) {
        this.mDelay = 0;
        this.mTimeout = timeout;
        this.mTimer = new Timer();
        this.mTaskListeners = new ArrayList<TaskListener>();
    }

    public Task() {
        this(DEFAULT_TIME_OUT);
    }

    public final String getID() {
        return StringUtil.newGuid();
    }

    @Override
    protected final void onPreExecute() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, mTimeout + mDelay);
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
    protected final void onPostExecute(TaskResult result) {
        if (mReturn) {
            return;
        }

        if (result == null) {
            result = new TaskResult(TaskStatus.FAILED, "TaskResult is null");
        }

        onTaskFinished(this);

        if (result.getStatus() == TaskStatus.SUCCEED) {
            onTaskSucceed(this, new TaskSucceedEvent(result.getContext()));
        } else if (result.getStatus() == TaskStatus.CHANCEL) {
            onTaskCancel(this, new TaskCancelEvent(result.getMessage()));
        } else {
            onTaskFailed(this, new TaskFailedEvent(result.getMessage(), result.getContext()));
        }
    }

    @Override
    protected void onCancelled(TaskResult result) {
        if (mReturn) {
            return;
        }

        onTaskFinished(this);

        onTaskCancel(this, new TaskCancelEvent("Task cancelled"));
    }

    public void setTimeout(int timeout) {
        this.mTimeout = timeout;
    }

    public void setReturnContext(TaskContext context) {
        this.mReturnContext = context;
    }

    public void setDelay(int delay) {
        this.mDelay = delay;
    }

    public void addTaskListener(TaskListener listener) {
        mTaskListeners.add(listener);
    }

    public void removeTaskCancelListener(TaskListener listener) {
        mTaskListeners.remove(listener);
    }

    protected void onTaskFinished(Task sender) {
        for (TaskListener listener : mTaskListeners) {
            listener.onTaskFinished(sender);
        }
    }

    protected void onTaskSucceed(Task sender, TaskSucceedEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (TaskListener listener : mTaskListeners) {
                listener.onTaskSucceed(sender, event);
            }
        }
    }

    protected void onTaskFailed(Task sender, TaskFailedEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (TaskListener listener : mTaskListeners) {
                listener.onTaskFailed(sender, event);
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (mReturn) {
            return;
        }

        onTaskCancel(this, new TaskCancelEvent("Cancelled"));
    }

    protected void onTaskProgressChanged(Task sender, TaskProgressChangedEvent event) {
        if (!mReturn) {
            for (TaskListener listener : mTaskListeners) {
                listener.onProgressChanged(sender, event);
            }
        }
    }

    protected void onTaskTimeout(Task sender, TaskTimeoutEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (TaskListener listener : mTaskListeners) {
                listener.onTimeout(sender, event);
            }
        }
    }

    protected void onTaskCancel(Task sender, TaskCancelEvent event) {
        if (!mReturn) {
            mReturn = true;
            for (TaskListener listener : mTaskListeners) {
                listener.onTaskCancel(sender, event);
            }
        }
    }

    public interface TaskListener {

        void onTaskFinished(Task sender);

        void onTaskSucceed(Task sender, TaskSucceedEvent event);

        void onTaskFailed(Task sender, TaskFailedEvent event);

        void onProgressChanged(Task sender, TaskProgressChangedEvent event);

        void onTimeout(Task sender, TaskTimeoutEvent event);

        void onTaskCancel(Task sender, TaskCancelEvent event);
    }

    public abstract static class BaseTaskListener implements TaskListener {

        @Override
        public void onTaskFinished(Task sender) {

        }

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {

        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {

        }

        @Override
        public void onProgressChanged(Task sender, TaskProgressChangedEvent event) {

        }

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {

        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {

        }

    }
}
