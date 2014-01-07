package com.pplive.liveplatform.ui.live.record;

import java.nio.ByteBuffer;

import com.pplive.liveplatform.Constants;
import com.pplive.sdk.MediaSDK;

import android.annotation.TargetApi;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PPboxAudioStream extends PPboxStream {
    

    public PPboxAudioStream(long capture, int itrack, AudioRecord audio) {
        mStreamType = "Audio";

        this.mCaptureId = capture;

        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            MediaFormat format = MediaManager.getInstance().getSupportedEncodingAudioFormat(MediaManager.MIME_TYPE_AUDIO_AAC, audio.getSampleRate(),
                    audio.getChannelCount());
            mEncoder = MediaCodec.createEncoderByType(MediaManager.MIME_TYPE_AUDIO_AAC);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        }

        mInSize = frame_size(1024, audio.getChannelConfiguration(), audio.getAudioFormat());

        mStreamInfo = new MediaSDK.StreamInfo();
        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            mStreamInfo.time_scale = 1000 * 1000;
        }
        
        mStreamInfo.bitrate = 0;
        mStreamInfo.__union0 = audio.getChannelCount();
        mStreamInfo.__union1 = 8 * (4 - audio.getAudioFormat());
        mStreamInfo.__union2 = audio.getSampleRate();
        mStreamInfo.__union3 = 1024;
        
        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            mStreamInfo.format_size = 0;
            mStreamInfo.format_buffer = ByteBuffer.allocateDirect(0);
        }

        mSample = new MediaSDK.Sample();
        mSample.itrack = itrack;
        mSample.flags = 0;
        mSample.time = 0;
        mSample.composite_time_delta = 0;
        mSample.size = mInSize;
        mSample.buffer = null;
    }

    
    @Override
    public void start() {
        super.start();
        
        
    }
}
