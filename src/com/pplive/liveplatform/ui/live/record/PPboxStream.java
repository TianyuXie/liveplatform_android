package com.pplive.liveplatform.ui.live.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.pplive.sdk.MediaSDK;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PPboxStream {

    protected static final String TAG = PPboxStream.class.getSimpleName();

    public class InBuffer {
        int mIndex;
        ByteBuffer mByteBuffer;

        public InBuffer(int index, int cap) {
            mIndex = index;
            mByteBuffer = ByteBuffer.allocateDirect(cap);
        }

        public InBuffer(int index, ByteBuffer bb) {
            mIndex = index;
            mByteBuffer = bb;
        }

        int size() {
            return mByteBuffer.capacity();
        }

        int index() {
            return mIndex;
        }

        ByteBuffer byte_buffer() {
            return mByteBuffer;
        }
    }

    protected long mCaptureId;

    protected MediaCodec mEncoder;

    protected InBuffer[] mInBuffers;

    protected ByteBuffer[] mOutBuffers;

    protected MediaCodec.BufferInfo mBufferInfo;

    protected Cycle<InBuffer> mCycleBuffer;

    protected int mInSize;

    protected MediaSDK.StreamInfo mStreamInfo;

    protected MediaSDK.Sample mSample;

    protected String mStreamType;

    public void start() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            mEncoder.start();
            ByteBuffer[] buffers = mEncoder.getInputBuffers();
            mInBuffers = new InBuffer[buffers.length];
            for (int i = 0; i < buffers.length; ++i) {
                mInBuffers[i] = new InBuffer(i, buffers[i]);
            }
            mOutBuffers = mEncoder.getOutputBuffers();
            mBufferInfo = new MediaCodec.BufferInfo();
        } else {
            mInBuffers = new InBuffer[128];
            mCycleBuffer = new Cycle<InBuffer>(mInBuffers.length);
            for (int i = 0; i < mInBuffers.length; ++i) {
                mInBuffers[i] = new InBuffer(i, mInSize);
                mCycleBuffer.push(mInBuffers[i]);
            }
        }
    }

    public void stop() {
        if (null != mEncoder) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
    }

    public int bufferCount() {
        return mInBuffers.length;
    }

    public int bufferSize() {
        return mInSize;
    }

    public InBuffer pop() {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            int index = mEncoder.dequeueInputBuffer(0);
            if (index >= 0) {
                return mInBuffers[index];
            } else {
                return null;
            }
        } else {
            return mCycleBuffer.pop();
        }
    }

    public void put(long time, InBuffer buffer) {
        mSample.time = time;
        put2(buffer);
    }

    private void put2(InBuffer buffer) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            mEncoder.queueInputBuffer(buffer.index(), 0, mInSize, mSample.time, 0);
            buffer.byte_buffer().clear();
            int index = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            if (index >= 0) {
                mSample.time = mBufferInfo.presentationTimeUs;
                mSample.flags = 0;
                if (mBufferInfo.flags != 0) {
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        Log.d(TAG, "Codec Config " + " itrack: " + mSample.itrack + " size: " + mBufferInfo.size);
                        mStreamInfo.format_size = mBufferInfo.size;
                        mStreamInfo.format_buffer = mOutBuffers[index];
                        MediaSDK.CaptureSetStream(mCaptureId, mSample.itrack, mStreamInfo);
                        mEncoder.releaseOutputBuffer(index, false);

                        return;
                    } else {
                        mSample.flags = 1;
                    }
                }
                mSample.size = mBufferInfo.size;
                mSample.buffer = mOutBuffers[index];

                // TOBO: DEBUG
                //                writeBuffer(mSample.buffer, mBufferInfo.size);

                MediaSDK.CapturePutSample(mCaptureId, mSample);

                mEncoder.releaseOutputBuffer(index, false);

                Log.d(TAG, String.format("[%s] time: %s; size: %s; flag: %d", mStreamType, mSample.time, mSample.size, mBufferInfo.flags));
            }
        } else {
            mSample.buffer = buffer.byte_buffer();

            MediaSDK.CapturePutSample(mCaptureId, mSample);
        }
    }

    private int mWritedBufferCount = 0;

    @SuppressLint("SdCardPath")
    private void writeBuffer(ByteBuffer buffer, int size) {

        if (mWritedBufferCount++ < 10) {

            FileOutputStream fos = null;
            if (mWritedBufferCount == 1) {
                try {
                    fos = new FileOutputStream(new File("/sdcard/buff/" + mStreamType + "_format_buff"));
                    byte[] dst = new byte[mStreamInfo.format_size];
                    mStreamInfo.format_buffer.get(dst);
                    fos.write(dst, 0, mStreamInfo.format_size);
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
                fos = new FileOutputStream(new File("/sdcard/buff/" + mStreamType + mWritedBufferCount));
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
        //        sample.decode_time += sample.duration;
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

}
