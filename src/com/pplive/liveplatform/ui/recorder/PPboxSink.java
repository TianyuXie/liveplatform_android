package com.pplive.liveplatform.ui.recorder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pplive.liveplatform.ui.recorder.PPboxStream.OnConfiguredListener;
import com.pplive.sdk.MediaSDK;
import com.pplive.sdk.MediaSDK.Download_Callback;
import com.pplive.thirdparty.BreakpadUtil;

public class PPboxSink {
    
    private static final String TAG = PPboxSink.class.getSimpleName();

    private long capture;

    private Camera camera;

    private AudioRecord audio;

    private PPboxStream video_stream;

    private PPboxStream audio_stream;

    private Thread audio_thread;
    
    private int configured_stream = 0;
    
    public OnConfiguredListener mOnConfiguredListener = new OnConfiguredListener() {
        
        @Override
        public void onConfigured() {
            configured_stream++;
            
            if (configured_stream >= 2 && video_stream != null & audio_stream != null) {
                video_stream.setReady(true);
                audio_stream.setReady(true);
            }
        }
    };

    public static void init(Context c) {
        BreakpadUtil.registerBreakpad(new File("/sdcard/pplog"));
        
        
        File cacheDirFile = c.getCacheDir();
        // String cacheDir = cacheDirFile.getAbsolutePath();
        String dataDir = cacheDirFile.getParentFile().getAbsolutePath();
        String libDir = dataDir + "/lib";
        String tmpDir = System.getProperty("java.io.tmpdir") + "/ppsdk";
        File tmpDirFile = new File(tmpDir);
        tmpDirFile.mkdir();

        MediaSDK.libPath = libDir;
        // cacheDir.getAbsolutePath();
        MediaSDK.logPath = tmpDir;
        MediaSDK.logLevel = MediaSDK.LEVEL_EVENT;
        MediaSDK.startP2PEngine("161", "12", "111");
    }

    public PPboxSink(Camera camera) {
        this.camera = camera;
    }

    public void open(String url) {
        capture = MediaSDK.CaptureOpen("pprecord://record", "rtmp", url, new Download_Callback() {
            
            @Override
            public void invoke(long err) {
                Log.d(TAG, "err:  " + err);
            }
        });

        // TODO: DEBUG
        //        camera = Camera.open();

        //        Camera.Parameters p = camera.getParameters();
        //        p.setPreviewFormat(ImageFormat.NV21);
        // List<int[]> fps_ranges = p.getSupportedPreviewFpsRange();
        // p.setPreviewFpsRange(5000, 15000);
        //        Camera.Size size = min_size(p.getSupportedPreviewSizes());
        //        p.setPreviewSize(size.width, size.height);
        //        camera.setParameters(p);

        audio = get_audio_record();

        MediaSDK.CaptureConfigData config = new MediaSDK.CaptureConfigData();

        // TODO: Debug
        config.stream_count = 2;
        config.thread_count = 2; // multi_thread
        config.sort_type = 1;
//        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
//            config.free_sample = null;
//        } else {
//            config.free_sample = new PPBOX.FreeSampleCallBack() {
//                @Override
//                public boolean invoke(long context) {
//                    return PPboxStream.free_sample(context);
//                }
//            };
//        }

        MediaSDK.CaptureInit(capture, config);

        video_stream = new PPboxStream(capture, 0, camera, mOnConfiguredListener);

        // TODO: Debug
        audio_stream = new PPboxStream(capture, 1, audio, mOnConfiguredListener);
    }

    public void preview(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        video_stream.start();
        final byte[] video_buffer = new byte[video_stream.buffer_size()];
        camera.addCallbackBuffer(video_buffer);

        // TODO: Debug
        audio_stream.start();

        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            private final long time_scale = 1000 * 1000 * 1000;
            private long start_time = System.nanoTime();
            private int num_total = 0;
            private int num_drop = 0;
            private long next_time = 5 * time_scale;

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                ++num_total;
                long time = System.nanoTime() - start_time;
                PPboxStream.InBuffer buffer = video_stream.pop();
                if (buffer == null) {
                    ++num_drop;
                    // System.out.println("video drop");
                } else {
                    buffer.byte_buffer().put(data);
                    video_stream.put(time / 1000, buffer);
                }
                if (time >= next_time) {
                    System.out.println("video " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: "+ num_drop);
                    next_time += 5 * time_scale;
                }
                camera.addCallbackBuffer(data);
            }
        });

        audio_thread = new Thread() {
            @Override
            public void run() {
                audio_read_thread();
            }
        };
        audio_thread.setPriority(Thread.MAX_PRIORITY);

        // TODO: Debug
        audio_thread.start();
    }

    private void audio_read_thread() {
        final long time_scale = 1000 * 1000 * 1000;
        final int read_size = audio_stream.buffer_size();
        final long start_time = System.nanoTime();
        int num_total = 0;
        int num_drop = 0;
        long next_time = 5 * time_scale;

        ByteBuffer drop_buffer = ByteBuffer.allocateDirect(read_size);

        audio.startRecording();
        while (!Thread.interrupted()) {
            long time = System.nanoTime() - start_time;
            if (time >= next_time) {
                Log.d(TAG, "audio " + " time:" + next_time / time_scale + " total: " + num_total + " accept: " + (num_total - num_drop) + " drop: "+ num_drop);
                next_time += 5 * time_scale;
            }
            ++num_total;
            PPboxStream.InBuffer buffer = audio_stream.pop();
            if (buffer == null) {
                // System.out.println("audio drop");
                audio.read(drop_buffer, read_size);
                audio_stream.drop();
                ++num_drop;
                continue;
            }
            int read = audio.read(buffer.byte_buffer(), read_size);
            if (read != read_size) {
                Log.d(TAG, "audio.read failed. read = " + read);
                break;
            }
            
            audio_stream.put(time / 1000, buffer);
        }
        audio.stop();
    }

    public void stop() {
        audio_thread.interrupt();
        try {
            audio_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        camera.setPreviewCallbackWithBuffer(null);

        // TODO: DEBUG
        //        camera.stopPreview();
    }

    public void close() {
        //        camera.release();

        // TODO: DEBUG
        //        audio.release();

        MediaSDK.CaptureDestroy(capture);

        video_stream.stop();
        audio_stream.stop();

        video_stream = null;
        audio_stream = null;
    }

    private static int[] sampleRates = new int[] { 8000, 11025, 22050, 44100 };
    private static short[] channelConfigs = new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO };
    private static short[] audioFormats = new short[] { AudioFormat.ENCODING_PCM_16BIT };

    private AudioRecord get_audio_record() {
        for (int sampleRate : sampleRates) {
            for (short channelConfig : channelConfigs) {
                for (short audioFormat : audioFormats) {
                    try {
                        Log.d(TAG, "Attempting rate " + sampleRate + "Hz, channel: " + channelConfig + ", bits: " + audioFormat);
                        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                        if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }
                        
                        Log.d(TAG, "bufferSize: " + bufferSize);
                        

                        bufferSize = PPboxStream.frame_size(1024, channelConfig, audioFormat) * 16;
                        // check if we can instantiate and have a success
                        AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, bufferSize);
                        
                        Log.d(TAG, "state: " + recorder.getState());
                        
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

    private Camera.Size min_size(List<Camera.Size> sizes) {
        Camera.Size ms = sizes.get(0);
        for (Camera.Size s : sizes) {
            if (s.width >= 320 && (ms.width < 320 || s.width < ms.width || s.height < ms.height)) {
                ms = s;
            }
        }
        return ms;
    }
}
