package com.pplive.liveplatform.widget.chat;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.api.comment.model.FeedItem;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskCancelEvent;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskFailedEvent;
import com.pplive.liveplatform.task.TaskSucceedEvent;
import com.pplive.liveplatform.task.TaskTimeoutEvent;
import com.pplive.liveplatform.task.Task.BaseTaskListener;
import com.pplive.liveplatform.task.Task.TaskListener;
import com.pplive.liveplatform.task.feed.GetFeedTask;

public class ChatBox extends RelativeLayout implements Handler.Callback {
    static final String TAG = "_ChatBox";

    private final static int MSG_GET_FEED = 2002;

    private final static int DEFAULT_REFRESH_DELAY = 5000;

    private final static int DEFAULT_RETRY_DELAY = 10000;

    private Handler mFeedHandler = new Handler(this);

    private List<FeedItem> mFeedItems;

    private ChatContentAdapter mAdapter;

    private TextView mNoContentInfo;

    private TaskContext mFeedContext;

    private boolean mStart;

    private int mUserColor;

    private int mContentColor;

    private int mOwnerColor;

    private int mRefreshDelay;

    private int mRetryDelay;

    private INewMessageListener mNewMessageListener;

    private long mTopFid;

    private OnSingleTapListener mSingleTapListener;

    private TaskListener mOnGetFeedListener = new BaseTaskListener() {

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            Log.d(TAG, "FeedTask onTimeout");
            refresh(0);
        }

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            Log.d(TAG, "FeedTask onTaskFinished");
            if (mStart) {
                FeedDetailList feeds = (FeedDetailList) event.getContext().get(GetFeedTask.KEY_RESULT);
                if (feeds != null) {
                    //                    mTextView.setText("");
                    List<FeedItem> contents = feeds.getFeedItems(UserManager.getInstance(getContext()).getUsernamePlain(), mUserColor, mContentColor,
                            mOwnerColor);
                    if (contents.size() != 0) {
                        mNoContentInfo.setVisibility(View.GONE);
                        mFeedItems.clear();
                        mFeedItems.addAll(contents);
                        mAdapter.notifyDataSetChanged();
                        //                        for (String content : contents) {
                        //                            mTextView.append(Html.fromHtml(content.toString()));
                        //                        }
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
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Log.d(TAG, "FeedTask onTaskFailed: " + event.getMessage());
            refresh(mRetryDelay);
        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
            Log.d(TAG, "FeedTask onTaskCancel");
            refresh(mRetryDelay);
        }

    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
		@Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mSingleTapListener != null) {
                    mSingleTapListener.onSingleTap();
                }
            }
            return false;
        }
    };

    public ChatBox(Context context) {
        this(context, null);
    }

    public ChatBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFeedContext = new TaskContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.widget_chatbox, this);
        mNoContentInfo = (TextView) root.findViewById(R.id.text_chatbox_nocontent);

        // init values
        int paddingLeft = 0;
        int paddingRight = 0;
        int paddingTop = 0;
        int paddingBottom = 0;
        float textSize = 16.0f;

        mRefreshDelay = DEFAULT_REFRESH_DELAY;
        mRetryDelay = DEFAULT_RETRY_DELAY;
        mFeedItems = new ArrayList<FeedItem>();
        mAdapter = new ChatContentAdapter(context, mFeedItems);

        ListView listView = (ListView) root.findViewById(R.id.list_chatbox);
        listView.setAdapter(mAdapter);
        listView.setOnTouchListener(mOnTouchListener);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChatBox);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.ChatBox_textSize:
                textSize = a.getDimensionPixelSize(attr, 16);
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
                    listView.setBackgroundResource(background);
                }
                break;
            case R.styleable.ChatBox_userColor:
                mUserColor = a.getColor(attr, getResources().getColor(android.R.color.white));
                break;
            case R.styleable.ChatBox_contentColor:
                mContentColor = a.getColor(attr, getResources().getColor(android.R.color.white));
                break;
            case R.styleable.ChatBox_ownerColor:
                mOwnerColor = a.getColor(attr, getResources().getColor(android.R.color.white));
                break;
            case R.styleable.ChatBox_defaultText:
                mNoContentInfo.setText(a.getString(attr));
                break;
            }
        }
        a.recycle();
        listView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        mAdapter.setTextSize(textSize);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_GET_FEED:
            if (mStart) {
                Log.d(TAG, "Get feed");
                GetFeedTask feedTask = new GetFeedTask();
                feedTask.addTaskListener(mOnGetFeedListener);
                feedTask.execute(mFeedContext);
            }
            mFeedHandler.removeMessages(MSG_GET_FEED);
            return true;
        }

        return false;
    }

    public void start(long pid) {
        Log.d(TAG, "start");
        mStart = true;
        mTopFid = -1;
        mFeedHandler.removeMessages(MSG_GET_FEED);
        GetFeedTask feedTask = new GetFeedTask();
        feedTask.addTaskListener(mOnGetFeedListener);
        mFeedContext.set(Extra.KEY_PROGRAM_ID, pid);
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
        return mFeedItems.isEmpty();
    }

    public void setOnSingleTapListener(OnSingleTapListener listener) {
        this.mSingleTapListener = listener;
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    };

    public interface OnSingleTapListener {
        public void onSingleTap();
    }
}
