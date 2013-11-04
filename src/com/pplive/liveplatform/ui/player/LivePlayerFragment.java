package com.pplive.liveplatform.ui.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.pplive.media.player.MeetVideoView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.pplive.liveplatform.R;

public class LivePlayerFragment extends Fragment implements OnTouchListener {
    static final String TAG = "LivePlayerFragment";

    private MeetVideoView mVideoView;

    private LivePlayerController mController;

    private OnCompletionListener mOnCompletionListener;

    private OnErrorListener mOnErrorListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.layout_player_fragment, container, false);
        mController = (LivePlayerController) layout.findViewById(R.id.live_player_controller);
        mVideoView = (MeetVideoView) layout.findViewById(R.id.live_player_videoview);
        layout.setOnTouchListener(this);
        return layout;
    }

    public void setupPlayer(Intent intent) {
        Uri uri = intent.getData();
        // uri =
        // Uri.parse("http://111.1.16.24/youku/69785C2C54A3E71A67BB168D6/0300080E0A51091C3469AA05CF07DDCC5586BD-6A9D-9FDD-5D28-E0EC7596689D.mp4");
        uri = Uri.parse("file:///mnt/sdcard/external_sd/movies/test1.mp4");
        mVideoView.setVideoURI(uri);
        mVideoView.setOnPreparedListener(mPreparedListener);
        mVideoView.setOnCompletionListener(mCompletionListener);
        mVideoView.setOnErrorListener(mErrorListener);
        mController.setMediaPlayer(mVideoView);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mVideoView.resume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        mVideoView.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        mVideoView.stopPlayback();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private MeetVideoView.OnPreparedListener mPreparedListener = new MeetVideoView.OnPreparedListener() {
        @Override
        public void onPrepared() {
            mVideoView.start();
            mController.show();
        }
    };

    private MeetVideoView.OnCompletionListener mCompletionListener = new MeetVideoView.OnCompletionListener() {
        public void onCompletion() {
            mVideoView.stopPlayback();
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion();
            }
        }
    };

    private MeetVideoView.OnErrorListener mErrorListener = new MeetVideoView.OnErrorListener() {
        public boolean onError(int what, int extra) {
            mVideoView.stopPlayback();
            if (mOnErrorListener != null) {
                return mOnErrorListener.onError(what, extra);
            }
            return false;
        }
    };

    public interface OnCompletionListener {
        void onCompletion();
    }

    public interface OnErrorListener {
        boolean onError(int what, int extra);
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        if (mDoubleTapListener.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    private GestureDetector mDoubleTapListener = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTap");
            mController.switchVisibility();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(TAG, "onDoubleTap");
            mVideoView.switchDisplayMode();
            return true;
        }
    });

}
