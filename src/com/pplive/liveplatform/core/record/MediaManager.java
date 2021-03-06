package com.pplive.liveplatform.core.record;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MediaManager {

    static final String TAG = MediaManager.class.getSimpleName();

    static final String QCOM_ENCODER = "OMX.qcom.video.encoder.avc";

    static final String TI_ENCODER = "OMX.TI.DUCATI1.VIDEO.H264E";

    public static final String MIME_TYPE_VIDEO_AVC = "video/avc";

    public static final String MIME_TYPE_AUDIO_AAC = "audio/mp4a-latm";

    public static final int IFRAME_INTERVAL = 1;

    public static final int[] SAMPLE_RATES = new int[] { 22050, 44100, 11025, 8000 };

    public static final short[] CHANNEL_CONFIGS = new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO };

    public static final short[] BIT_PER_SAMPLE = new short[] { AudioFormat.ENCODING_PCM_16BIT };

    public static MediaManager sInstance = new MediaManager();

    public static MediaManager getInstance() {
        return sInstance;
    }

    private MediaManager() {
    };

    public MediaFormat getSupportedEncodingVideoFormat(final String mime, final Camera.Size size, final Quality quality) {
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; ++i) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);

            String[] types = info.getSupportedTypes();

            for (int j = 0; j < types.length; ++j) {

                if (info.isEncoder() && types[j].equals(mime)) {
                    CodecCapabilities caps = info.getCapabilitiesForType(types[j]);

                    int[] colorFormats = caps.colorFormats;
                    int colorFormat = colorFormats[0];
                    for (int k = 0; k < colorFormats.length; ++k) {
                        Log.d(TAG, "color: " + colorFormats[k]);
                        if (colorFormats[k] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
                                || colorFormats[k] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                            colorFormat = colorFormats[k];
                            Log.d(TAG, "encoder: " + info.getName());
                        }
                    }

                    Log.d(TAG, "colorFormat: " + colorFormat);

                    MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE_VIDEO_AVC, size.width, size.height);
                    format.setInteger(MediaFormat.KEY_BIT_RATE, quality.getVideoBitrate());
                    format.setInteger(MediaFormat.KEY_FRAME_RATE, quality.getFrameRate());
                    format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
                    format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);

                    return format;
                }
            }
        }

        return null;
    }

    public MediaFormat getSupportedEncodingAudioFormat(final String mime, final int sampleRate, final int channelCount, final Quality quality) {
        MediaFormat format = MediaFormat.createAudioFormat(mime, sampleRate, channelCount);
        format.setInteger(MediaFormat.KEY_BIT_RATE, quality.getAudioBitrate());

        return format;
    }

    public AudioRecord getAudioRecord() {
        for (int sampleRate : SAMPLE_RATES) {
            for (short channelConfig : CHANNEL_CONFIGS) {
                for (short bitSample : BIT_PER_SAMPLE) {
                    try {
                        Log.d(TAG, "Attempting rate " + sampleRate + "Hz, channel: " + channelConfig + ", bits: " + bitSample);
                        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, bitSample);

                        if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }

                        Log.d(TAG, "bufferSize: " + bufferSize);

                        bufferSize = PPboxStream.frame_size(1024, channelConfig, bitSample) * 16;
                        // check if we can instantiate and have a success
                        AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, sampleRate, channelConfig, bitSample, bufferSize);

                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                            Log.d(TAG, "Supported. buffer size: " + bufferSize);
                            return recorder;
                        }

                        Log.d(TAG, "release audio recorder");
                        recorder.release();
                    } catch (Exception e) {
                        Log.d(TAG, "Exception, keep trying." + e);
                    }
                }
            }
        }
        return null;
    }
}
