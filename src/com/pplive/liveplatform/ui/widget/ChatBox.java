package com.pplive.liveplatform.ui.widget;

import java.util.Collection;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.player.GetFeedTask;

public class ChatBox extends RelativeLayout {
    static final String TAG = "_ChatBox";

    private final static int MSG_GET_FEED = 2002;

    private final static int DEFAULT_REFRESH_DELAY = 5000;

    private final static int DEFAULT_RETRY_DELAY = 10000;

    private TextView mTextView;

    private TextView mNoContentInfo;

    private TaskContext mFeedContext;

    private boolean mStart;

    private int mUserColor;

    private int mContentColor;

    private int mRefreshDelay;

    private int mRetryDelay;

    private INewMessageListener mNewMessageListener;

    private long mTopFid;

    public ChatBox(Context context) {
        this(context, null);
    }

    public ChatBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFeedContext = new TaskContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.widget_chatbox, this);
        mTextView = (TextView) root.findViewById(R.id.text_chatbox);
        mNoContentInfo = (TextView) root.findViewById(R.id.text_chatbox_nocontent);
        View scrollView = root.findViewById(R.id.scroll_chatbox);

        // init values
        int paddingLeft = 0;
        int paddingRight = 0;
        int paddingTop = 0;
        int paddingBottom = 0;
        mRefreshDelay = DEFAULT_REFRESH_DELAY;
        mRetryDelay = DEFAULT_RETRY_DELAY;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChatBox);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.ChatBox_textSize:
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(attr, 16));
                mNoContentInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(attr, 16));
                break;
            case R.styleable.ChatBox_paddingLeft:
                paddingLeft = a.getDimensionPixelSize(attr, 0);
                break;
            case R.styleable.ChatBox_paddingRight:
                paddingRight = a.getDimensionPixelSize(attr, 0);
                break;
            case R.styleable.ChatBox_paddingTop:
                paddingTop = a.getDimensionPixelSize(attr, 0);
                break;
            case R.styleable.ChatBox_paddingBottom:
                paddingBottom = a.getDimensionPixelSize(attr, 0);
                break;
            case R.styleable.ChatBox_refreshDelay:
                mRefreshDelay = a.getInteger(attr, DEFAULT_REFRESH_DELAY);
                break;
            case R.styleable.ChatBox_retryDelay:
                mRetryDelay = a.getInteger(attr, DEFAULT_RETRY_DELAY);
                break;
            case R.styleable.ChatBox_background:
                int background = a.getResourceId(attr, -1);
                if (background > 0) {
                    scrollView.setBackgroundResource(background);
                }
                break;
            case R.styleable.ChatBox_textColor:
                int color = a.getColor(attr, getResources().getColor(R.color.white));
                mTextView.setTextColor(color);
                mNoContentInfo.setTextColor(color);
                break;
            case R.styleable.ChatBox_userColor:
                mUserColor = a.getColor(attr, getResources().getColor(R.color.white));
                break;
            case R.styleable.ChatBox_contentColor:
                mContentColor = a.getColor(attr, getResources().getColor(R.color.white));
                break;
            case R.styleable.ChatBox_lineSpacingMultiplier:
                float mult = a.getFloat(attr, 1.0f);
                mTextView.setLineSpacing(0.0f, mult);
                break;
            }
        }
        a.recycle();
        scrollView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private Task.OnTaskListener onGetFeedListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "FeedTask onTimeout");
            refresh(0);
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "FeedTask onTaskFinished");
            if (mStart) {
                FeedDetailList feeds = (FeedDetailList) event.getContext().get(GetFeedTask.KEY_RESULT);
                if (feeds != null) {
                    mTextView.setText("");
                    Collection<String> contents = feeds.getFeedStrings(mUserColor, mContentColor);
                    if (contents.size() != 0) {
                        mNoContentInfo.setVisibility(View.GONE);
                        for (String content : contents) {
                            mTextView.append(Html.fromHtml(content.toString()));
                        }
                        long topFid = feeds.getTopFeedId();
                        if (topFid > 0 && topFid != mTopFid) {
                            mTopFid = topFid;
                            if (mNewMessageListener != null) {
                                mNewMessageListener.notifyMessage();
                            }
                        }
                    } else {
                        mNoContentInfo.setVisibility(View.VISIBLE);
                        mTopFid = -1;
                    }
                }
            }
            refresh(mRefreshDelay);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "FeedTask onTaskFailed: " + event.getMessage());
            refresh(mRetryDelay);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "FeedTask onTaskCancel");
            refresh(mRetryDelay);
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private Handler mFeedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GET_FEED:
                if (mStart) {
                    Log.d(TAG, "Get feed");
                    GetFeedTask feedTask = new GetFeedTask();
                    feedTask.addTaskListener(onGetFeedListener);
                    feedTask.execute(mFeedContext);
                }
                mFeedHandler.removeMessages(MSG_GET_FEED);
                break;
            }
        }
    };

    public void start(long pid) {
        Log.d(TAG, "start");
        mStart = true;
        mTopFid = -1;
        mFeedHandler.removeMessages(MSG_GET_FEED);
        GetFeedTask feedTask = new GetFeedTask();
        feedTask.addTaskListener(onGetFeedListener);
        mFeedContext.set(Task.KEY_PID, pid);
        feedTask.execute(mFeedContext);
    }

    public void refresh(long delay) {
        mFeedHandler.removeMessages(MSG_GET_FEED);
        if (mStart) {
            mFeedHandler.sendEmptyMessageDelayed(MSG_GET_FEED, delay);
        }
    }

    public void stop() {
        Log.d(TAG, "stop");
        mStart = false;
        mFeedHandler.removeMessages(MSG_GET_FEED);
    }

    public void setDelay(int refreshDelay, int retryDelay) {
        mRefreshDelay = refreshDelay;
        mRetryDelay = retryDelay;
    }

    public interface INewMessageListener {
        public void notifyMessage();
    }
    

    public void setNewMessageListener(INewMessageListener l) {
        this.mNewMessageListener = l;
    }


    public boolean isEmpty() {
        return TextUtils.isEmpty(mTextView.getText().toString());
    }
}
