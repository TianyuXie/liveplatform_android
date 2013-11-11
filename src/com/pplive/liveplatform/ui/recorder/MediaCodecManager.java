package com.pplive.liveplatform.ui.recorder;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MediaCodecManager {

    public static final String MIME_TYPE_VIDEO_AVC = "video/avc";
    
    public static final String MIME_TYPE_AUIDO_AAC = "audio/mp4a-latm";
    
    public static final int BIT_RATE = 200000;
    
    public static final int FRAME_RATE = 15;
    
    public static final int IFRAME_INTERVAL = 5;
    
    public static MediaCodecManager sInstance = new MediaCodecManager();
    
    public static MediaCodecManager getInstance() {
        return sInstance;
    }

    private MediaCodecManager() {
    };
    
    public MediaFormat getSupportedEncodingMediaFormat(final String type, final Camera.Size size) {
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; ++i) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            
            String[] types = info.getSupportedTypes();
            
            for (int j = 0; j < types.length; ++j) {
                
                if (info.isEncoder() && types[j].equals(type)) {
                    CodecCapabilities caps = info.getCapabilitiesForType(types[j]);
                    
                    int[] colorFormats = caps.colorFormats;
                    int colorFormat = colorFormats[0];
                    for (int k = 0; k < colorFormats.length; ++k) {
                        if (MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar == colorFormats[k]) {
                            colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
                        }
                    }
                    
                    MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE_VIDEO_AVC, size.width, size.height);
                    format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
                    format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
                    format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
                    format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
                    
                    return format;
                }
            }
        }
        
        
        return null;
    }
}
