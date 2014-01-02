package com.pplive.liveplatform.core.service.live;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import android.util.Log;

import com.pplive.liveplatform.core.service.live.model.Packet;
import com.pplive.liveplatform.core.service.live.resp.PacketMapResp;
import com.pplive.liveplatform.util.URL;
import com.pplive.liveplatform.util.URL.Protocol;

public class UpdateService extends RestService {

    private static final String TAG = UpdateService.class.getSimpleName();

    private static final String TEMPLATE_CHECK_UPDATE = new URL(Protocol.HTTP, "android.config.synacast.com",
            "/check_update?channel={channel}&platform={platform}&osv={OSVersion}&sv={SWVersion}&devicetype={devicetype}").toString();

    private static final UpdateService sInstance = new UpdateService();

    public static UpdateService getInstance() {
        return sInstance;
    }

    public Packet checkUpdate(String channel, String platform, String OSVersion, String SWVersion, String deviceType) {
        Log.d(TAG, "channel: " + channel + "; platform: " + platform + "; OSVersion: " + OSVersion + "; SWVersion: " + SWVersion + "; deviceType: "
                + deviceType);

        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter() {

            @Override
            public boolean canRead(Class<?> clazz, MediaType mediaType) {
                return true;
            }
        });

        PacketMapResp resp = mRestTemplate.getForObject(TEMPLATE_CHECK_UPDATE, PacketMapResp.class, channel, platform, OSVersion, SWVersion, deviceType);

        Packet packet = resp.get("packet0");

        return packet;
    }
}
