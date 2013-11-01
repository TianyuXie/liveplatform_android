package com.pplive.liveplatform.ui.liveplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.ppmedia.widget.VideoView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pplive.liveplatform.R;

public class LivePlayerFragment extends Fragment {
    static final String TAG = "LivePlayerFragment";

    private VideoView mVideoView;

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
        View layout = inflater.inflate(R.layout.live_player_fragment, container, false);
        mController = (LivePlayerController) layout.findViewById(R.id.live_player_controller);
        mVideoView = (VideoView) layout.findViewById(R.id.live_player_videoview);
        return layout;
    }

    public void setupPlayer(Intent intent) {
        Uri uri = intent.getData();
        uri = Uri.parse("http://111.1.16.24/youku/69785C2C54A3E71A67BB168D6/0300080E0A51091C3469AA05CF07DDCC5586BD-6A9D-9FDD-5D28-E0EC7596689D.mp4");
        mVideoView.setVideoURI(uri);
        mVideoView.setOnCompletionListener(mCompletionListener);
        mVideoView.setOnErrorListener(mErrorListener);
        mController.setMediaPlayer(mVideoView);
    }

    public void playVideo() {
        mVideoView.start();
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

    private VideoView.OnCompletionListener mCompletionListener = new VideoView.OnCompletionListener() {
        public void onCompletion() {
            mVideoView.stopPlayback();
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion();
            }
        }
    };

    private VideoView.OnErrorListener mErrorListener = new VideoView.OnErrorListener() {
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
}
