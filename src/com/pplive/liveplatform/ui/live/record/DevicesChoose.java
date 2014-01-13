package com.pplive.liveplatform.ui.live.record;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DevicesChoose {
    
    public static String K3_ENCODER = "OMX.k3.video.encoder.avc";
    public static String NEXUS_ENCODER = "OMX.qcom.video.encoder.avc";
    public static String GOOGLE_ENCODER = "OMX.google.h264.encoder";
    public static String MTK_ENCODER= "OMX.MTK.VIDEO.ENCODER.AVC";
    public static String TI_ENCODER = "OMX.TI.DUCATI1.VIDEO.H264E";
    
    public static String XIAOMI = "";
    
    public static final int NOCHANGE = 0;
    public static final int YV12TOYUV420 = 1;
    public static final int NV12TONV21 = 2;
    public static final int YV12TOYUV420NV21 = 3;
    public static final int YV12TOI420 = 4;
    
    public static int getPreviewImageFormat(){
        String encoder = getEncodeInfoName();
        if(encoder.equals(K3_ENCODER)){
            return ImageFormat.NV21;
        }else if((encoder.equals(NEXUS_ENCODER) || encoder.equals(TI_ENCODER))){
            return ImageFormat.NV21;
        }else if(encoder.equals(GOOGLE_ENCODER)){
            return ImageFormat.YV12;
        }else if(encoder.equals(MTK_ENCODER)){
            return ImageFormat.YV12;
        }else{
            return ImageFormat.NV21;
        }
        
    }
    
    public static int getYUV420Switch(){
        String encoder = getEncodeInfoName();
        if(encoder.equals(NEXUS_ENCODER)
                || encoder.equals(TI_ENCODER)){
            return NV12TONV21;
        }else if(encoder.equals(GOOGLE_ENCODER)){
            return YV12TOYUV420;
        }else if(encoder.equals(MTK_ENCODER)){
            return YV12TOI420;
        }else {
            return NOCHANGE;
        }
    }
    
    public static String getEncodeInfoName() {
        String mime = "video/avc";
        String encoderName = "";
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; ++i) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            String[] types = info.getSupportedTypes();
            for (int j = 0; j < types.length; ++j) {
                if (info.isEncoder() && types[j].equals(mime)) {
                    encoderName = info.getName();
                    break;
                }
            }
        }
        return encoderName;
    }

}
