package com.pplive.liveplatform.ui.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.pplive.sdk.PPBOX;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PPboxStream {

    private static final String TAG = PPboxStream.class.getSimpleName();

    public class InBuffer {
        int index_;
        ByteBuffer byte_buffer_;

        public InBuffer(int index, int cap) {
            index_ = index;
            byte_buffer_ = ByteBuffer.allocateDirect(cap);
        }

        public InBuffer(int index, ByteBuffer bb) {
            index_ = index;
            byte_buffer_ = bb;
        }

        int size() {
            return byte_buffer_.capacity();
        }

        int index() {
            return index_;
        }

        ByteBuffer byte_buffer() {
            return byte_buffer_;
        }
    }

    private long capture;

    private MediaCodec encoder;

    private InBuffer[] in_buffers;

    private ByteBuffer[] out_buffers;

    private MediaCodec.BufferInfo buffer_info;

    private Cycle<InBuffer> buffer_cycle;

    private int in_size;

    private PPBOX.StreamInfo stream_info;

    private PPBOX.Sample sample;

    private String stream_type;

    private static PPboxStream[] streams = new PPboxStream[2];

    public PPboxStream(long capture, int itrack, Camera camera) {
        stream_type = "Video";

        this.capture = capture;
        streams[itrack] = this;

        Camera.Parameters p = camera.getParameters();

        p.getPreviewFormat();
        Log.d(TAG, "Preview Format: " + p.getPreviewFormat());

        Camera.Size size = p.getPreviewSize();
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            //MediaCodecInfo codec = find_codec("video/avc");
            //MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType("video/avc");
            //int[] clr_fmts = capabilities.colorFormats;
            MediaFormat format = MediaFormat.createVideoFormat("video/avc", size.width, size.height);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 200000);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
            encoder = MediaCodec.createEncoderByType("video/avc");
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        }

        in_size = pic_size(p);

        stream_info = new PPBOX.StreamInfo();
        stream_info.type = fourcc("VIDE");
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            stream_info.sub_type = fourcc("AVC1");
        } else {
            stream_info.sub_type = pic_format(p.getPreviewFormat());
        }
        stream_info.time_scale = 1000 * 1000;
        stream_info.bitrate = 0;
        stream_info.__union0 = p.getPreviewSize().width;
        stream_info.__union1 = p.getPreviewSize().height;
        stream_info.__union2 = 0;
        stream_info.__union3 = 1;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            stream_info.format_type = 2;
            stream_info.format_size = 0;
            stream_info.format_buffer = ByteBuffer.allocateDirect(0);
        } else {
            stream_info.format_type = 0;
            stream_info.format_size = 0;
            stream_info.format_buffer = ByteBuffer.allocateDirect(0);
        }

        PPBOX.CaptureSetStream(capture, itrack, stream_info);

        sample = new PPBOX.Sample();
        sample.itrack = itrack;
        sample.flags = 0;
        sample.time = 0;
        sample.decode_time = 0;
        sample.composite_time_delta = 0;
        sample.duration = 0;
        sample.size = in_size;
        sample.buffer = null;
        sample.context = 0;
    }

    public PPboxStream(long capture, int itrack, AudioRecord audio) {
        stream_type = "Audio";

        this.capture = capture;
        streams[itrack] = this;

        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            //MediaCodecInfo codec = find_codec("audio/mp4a-latm");
            //MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType("video/avc");

            Log.d(TAG, "[Audio] sample_rate: " + audio.getSampleRate() + "; channel_count: " + audio.getChannelCount());

            MediaFormat format = MediaFormat.createAudioFormat("audio/mp4a-latm", audio.getSampleRate(), audio.getChannelCount());
            format.setInteger(MediaFormat.KEY_BIT_RATE, 16000);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectELD);
            encoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        }

        in_size = frame_size(1024, audio.getChannelConfiguration(), audio.getAudioFormat());

        stream_info = new PPBOX.StreamInfo();
        stream_info.type = fourcc("AUDI");
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            stream_info.sub_type = fourcc("MP4A");
        } else {
            stream_info.sub_type = fourcc("PCM0");
        }
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            stream_info.time_scale = 1000 * 1000;
        } else {
            stream_info.time_scale = audio.getSampleRate();
        }
        stream_info.bitrate = 0;
        stream_info.__union0 = audio.getChannelCount();
        stream_info.__union1 = 8 * (4 - audio.getAudioFormat());
        stream_info.__union2 = audio.getSampleRate();
        stream_info.__union3 = 1024;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            stream_info.format_type = 0;
            stream_info.format_size = 0;
            stream_info.format_buffer = ByteBuffer.allocateDirect(0);
            ;
        } else {
            stream_info.format_type = 0;
            stream_info.format_size = 0;
            stream_info.format_buffer = ByteBuffer.allocateDirect(0);
        }

        PPBOX.CaptureSetStream(capture, itrack, stream_info);

        sample = new PPBOX.Sample();
        sample.itrack = itrack;
        sample.flags = 0;
        sample.time = 0;
        sample.decode_time = 0;
        sample.composite_time_delta = 0;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            sample.duration = 0;
        } else {
            sample.duration = stream_info.__union3;
        }
        sample.size = in_size;
        sample.buffer = null;
        sample.context = 0;
    }

    public void start() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            encoder.start();
            ByteBuffer[] buffers = encoder.getInputBuffers();
            in_buffers = new InBuffer[buffers.length];
            for (int i = 0; i < buffers.length; ++i) {
                in_buffers[i] = new InBuffer(i, buffers[i]);
            }
            out_buffers = encoder.getOutputBuffers();
            buffer_info = new MediaCodec.BufferInfo();
        } else {
            in_buffers = new InBuffer[128];
            buffer_cycle = new Cycle<InBuffer>(in_buffers.length);
            for (int i = 0; i < in_buffers.length; ++i) {
                in_buffers[i] = new InBuffer(i, in_size);
                buffer_cycle.push(in_buffers[i]);
            }
        }
    }

    public void stop() {
        if (null != encoder) {
            encoder.stop();
            encoder.release();
            encoder = null;
        }
    }

    public int buffer_count() {
        return in_buffers.length;
    }

    public int buffer_size() {
        return in_size;
    }

    public InBuffer pop() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            int index = encoder.dequeueInputBuffer(0);
            if (index >= 0) {
                return in_buffers[index];
            } else {
                return null;
            }
        } else {
            return buffer_cycle.pop();
        }
    }

    public void put(InBuffer buffer) {
        put2(buffer);
        sample.decode_time += sample.duration;
    }

    public void put(long time, InBuffer buffer) {
        sample.decode_time = time;
        put2(buffer);
    }

    private void put2(InBuffer buffer) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            encoder.queueInputBuffer(buffer.index(), 0, in_size, sample.decode_time, 0);
            buffer.byte_buffer().clear();
            int index = encoder.dequeueOutputBuffer(buffer_info, 0);
            if (index >= 0) {
                sample.decode_time = buffer_info.presentationTimeUs;
                sample.flags = 0;
                if (buffer_info.flags != 0) {
                    if (buffer_info.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        System.out.println("Codec Config " + " itrack: " + sample.itrack + " size: " + buffer_info.size);
                        stream_info.format_size = buffer_info.size;
                        stream_info.format_buffer = out_buffers[index];
                        PPBOX.CaptureSetStream(capture, sample.itrack, stream_info);
                        encoder.releaseOutputBuffer(index, false);
                        return;
                    } else {
                        sample.flags = 1;
                    }
                }
                sample.size = buffer_info.size;
                sample.buffer = out_buffers[index];

                // TOBO: DEBUG
                //                writeBuffer(sample.buffer, buffer_info.size);

                //sample.context = (((long)sample.itrack << 16) | ((long)index)) + 1;
                PPBOX.CapturePutSample(capture, sample);

                encoder.releaseOutputBuffer(index, false);

                Log.d(TAG, String.format("[%s] decode_time: %s; size: %s", stream_type, sample.decode_time, sample.size));
            }
        } else {
            sample.buffer = buffer.byte_buffer();
            sample.context = (((long) sample.itrack << 16) | ((long) buffer.index())) + 1;
            PPBOX.CapturePutSample(capture, sample);
        }
    }

    private int mWritedBufferCount = 0;

    private void writeBuffer(ByteBuffer buffer, int size) {

        if (mWritedBufferCount++ < 10) {

            FileOutputStream fos = null;
            if (mWritedBufferCount == 1) {
                try {
                    fos = new FileOutputStream(new File("/sdcard/buff/" + stream_type + "_format_buff"));
                    byte[] dst = new byte[stream_info.format_size];
                    stream_info.format_buffer.get(dst);
                    fos.write(dst, 0, stream_info.format_size);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (null != fos) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                fos = new FileOutputStream(new File("/sdcard/buff/" + stream_type + mWritedBufferCount));
                byte[] dst = new byte[size];
                buffer.get(dst);
                fos.write(dst, 0, size);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void drop() {
        sample.decode_time += sample.duration;
    }

    private boolean free_sample2(int index) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            encoder.releaseOutputBuffer(index, false);
        } else {
            InBuffer buffer = in_buffers[index];
            buffer.byte_buffer().clear();
            buffer_cycle.push(buffer);
        }
        return true;
    }

    public static boolean free_sample(long context) {
        --context;
        int itrack = (int) (context >> 16);
        int index = (int) (context & 0xffff);
        return streams[itrack].free_sample2(index);
    }

    public static int fourcc(String f) {
        byte[] bytes = f.getBytes();
        return (int) bytes[3] << 24 | (int) bytes[2] << 16 | (int) bytes[1] << 8 | (int) bytes[0];
    }

    public static int pic_format(int f) {
        switch (f) {
        case ImageFormat.NV16:
            f = fourcc("NV16");
            break;
        case ImageFormat.YV12:
            f = fourcc("YV12");
            break;
        case ImageFormat.YUY2:
            f = fourcc("YUY2");
            break;
        case ImageFormat.NV21:
            f = fourcc("NV21");
            break;
        case ImageFormat.RGB_565:
            f = fourcc("RGB5");
            break;
        }
        return f;
    }

    public static int pic_size(Camera.Parameters p) {
        Camera.Size size = p.getPreviewSize();
        int f = p.getPreviewFormat();
        if (f == ImageFormat.YV12) {
            int yStride = (int) Math.ceil(size.width / 16.0) * 16;
            int uvStride = (int) Math.ceil((yStride / 2) / 16.0) * 16;
            int ySize = yStride * size.height;
            int uvSize = uvStride * size.height / 2;
            return ySize + uvSize * 2;
        } else {
            int bpp = ImageFormat.getBitsPerPixel(f);
            return size.width * size.height * bpp / 8;
        }
    }

    public static int frame_size(int spf, int channel, int format) {
        return spf * (5 - channel / 4) * (4 - format);
    }

    public static MediaCodecInfo find_codec(String mineType) {
        for (int i = 0; i < MediaCodecList.getCodecCount(); ++i) {
            MediaCodecInfo codec = MediaCodecList.getCodecInfoAt(i);
            if (!codec.isEncoder()) {
                continue;
            }
            if (!codec.getName().startsWith("OMX.")) {
                continue;
            }
            String[] types = codec.getSupportedTypes();
            for (int j = 0; j < types.length; ++j) {
                System.out.println("Found codec " + codec.getName() + ", support type " + types[j]);
                if (types[j].equalsIgnoreCase(mineType)) {
                    System.out.println("Found codec for mine type " + mineType + " -> " + codec.getName());
                    return codec;
                }
            }
        }
        System.out.println("Not Found codec for mine type " + mineType);
        return null;
    }
}
