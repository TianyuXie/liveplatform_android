package com.pplive.liveplatform.core.record;

import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.sdk.MediaSDK;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PPboxAudioStream extends PPboxStream {
    
    private AudioRecord mAudioRecord;

    private Thread mAudioThread;

    public PPboxAudioStream(long capture, int itrack, long startTime, AudioRecord audio, Quality quality) {
        super(capture, startTime, quality);
        
        mStreamType = "Audio";

        mCaptureId = capture;
        
        mAudioRecord = audio;

        if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
            MediaFormat format = MediaManager.getInstance().getSupportedEncodingAudioFormat(MediaManager.MIME_TYPE_AUDIO_AAC, audio.getSampleRate(),
                    audio.getChannelCount(), mQuality);
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
        
        mAudioThread = new Thread() {
            @Override
            public void run() {
                audio_read_thread();
            }
        };
        mAudioThread.setPriority(Thread.MAX_PRIORITY);
        mAudioThread.start();
    }
    
    @Override
    public void stop() {
        
        mAudioThread.interrupt();
        try {
            mAudioThread.join();
        } catch (InterruptedException e) {
            Log.w(TAG, e.toString());
        }
        
        mAudioThread = null;
    }
    
    private void audio_read_thread() {
        final long time_scale = 1000 * 1000 * 1000;
        final int read_size = bufferSize();
        int num_total = 0;
        int num_drop = 0;
        long next_time = 5 * time_scale;

        ByteBuffer drop_buffer = ByteBuffer.allocateDirect(read_size);

        mAudioRecord.startRecording();
        while (!Thread.interrupted()) {
            long time = System.nanoTime() - mStartTime;
            if (time >= next_time) {
                Log.d(TAG, "audio " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: " + num_drop);
                next_time += 5 * time_scale;
            }
            ++num_total;
            PPboxStream.InBuffer buffer = pop();
            if (buffer == null) {
                // System.out.println("audio drop");
                mAudioRecord.read(drop_buffer, read_size);
                drop();
                ++num_drop;
                continue;
            }
            int read = mAudioRecord.read(buffer.byte_buffer(), read_size);
            if (read != read_size) {
                Log.d(TAG, "audio.read failed. read = " + read);
                break;
            }

            put(time / 1000, buffer);
        }

        mAudioRecord.stop();
    }
}
