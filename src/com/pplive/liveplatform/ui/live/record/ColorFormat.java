package com.pplive.liveplatform.ui.live.record;

public class ColorFormat {
    
    public static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output, final int width, final int height) {
        /* 
         * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12
         * We convert by putting the corresponding U and V bytes together (interleaved).
         */
        final int frameSize = width * height;
        final int qFrameSize = frameSize/4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        //for (int i = 0; i < qFrameSize; i++) {
        //    output[frameSize + i*2] = input[frameSize + i + qFrameSize]; // Cb (U)
        //    output[frameSize + i*2 + 1] = input[frameSize + i]; // Cr (V)
        //}
        
        for (int i = 0; i < (qFrameSize); i++) {  
            output[frameSize + i*2] = (input[frameSize + qFrameSize + i - 32 - 320]);  
            output[frameSize + i*2 + 1] = (input[frameSize + i - 32 - 320]);            
        }
        return output;
    }

    public static byte[] YV12toYUV420Planar(byte[] input, byte[] output, int width, int height) {
        /* 
         * COLOR_FormatYUV420Planar is I420 which is like YV12, but with U and V reversed.
         * So we just have to reverse U and V.
         */
        final int frameSize = width * height;
        final int qFrameSize = frameSize/4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y
        System.arraycopy(input, frameSize, output, frameSize + qFrameSize, qFrameSize); // Cr (V)
        System.arraycopy(input, frameSize + qFrameSize, output, frameSize, qFrameSize); // Cb (U)

        return output;
    }

    public static byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        byte[] i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width*height; i++)
            i420bytes[i] = yv12bytes[i];
        for (int i = width*height; i < width*height + (width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i + (width/2*height/2)];
        for (int i = width*height + (width/2*height/2); i < width*height + 2*(width/2*height/2); i++)
            i420bytes[i] = yv12bytes[i - (width/2*height/2)];
        return i420bytes;
    }
    
    
    public static void convertNV21toNV12(byte[] data) {
        if (null != data) {
            for (int i = data.length / 2 + 1; i + 2 < data.length; i+=2) {
                byte tmp = data[i];
                data[i] = data[i+1];
                data[i+1] = tmp;
            }
        }
    }
    
}
