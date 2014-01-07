package com.pplive.liveplatform.location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pplive.liveplatform.util.NetworkUtil;

public class BaiduLocator extends Locator implements BDLocationListener {
    static final String TAG = "BaiduLocator";

    private static final String DEFAULT_COUNTRY_ZH = "中国";

    private int gpsRetryTimes = 10;

    private int networkRetryTimes = 5;

    private int retryCount = 0;

    private int retryTimes = 5;

    private LocationClient locationClient;

    @Override
    public boolean setProvider(Context context) {
        Provider newProvider;
        if (NetworkUtil.isWifiConnected(context)) {
            newProvider = Provider.WIFI;
        } else if (NetworkUtil.isNetworkConnected(context)) {
            newProvider = Provider.MOBILE;
        } else if (NetworkUtil.isGpsEnable(context)) {
            newProvider = Provider.GPS;
        } else {
            newProvider = Provider.NONE;
        }
        boolean result = (!newProvider.equals(this.provider));
        this.provider = newProvider;
        return result;
    }

    public BaiduLocator(Context context) {
        this.locationClient = new LocationClient(context.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setScanSpan(networkScanSpan);
        retryTimes = networkRetryTimes;
        this.locationClient.setLocOption(option);
        this.locationClient.registerLocationListener(this);
    }

    @Override
    public void start(Context context) {
        Log.d(TAG, "start");
        if (setProvider(context)) {
            LocationClientOption option = locationClient.getLocOption();
            if (this.provider == Provider.GPS) {
                option.setOpenGps(true);
                option.setPriority(LocationClientOption.GpsFirst);
                option.setScanSpan(gpsScanSpan);
                retryTimes = gpsRetryTimes;
            } else {
                option.setOpenGps(false);
                option.setPriority(LocationClientOption.NetWorkFirst);
                option.setScanSpan(networkScanSpan);
                retryTimes = networkRetryTimes;
            }
            locationClient.setLocOption(option);
        }
        if (this.provider == Provider.NONE) {
            stop();
        } else {
            retryCount = 0;
            locationClient.start();
        }
    }

    public void stop() {
        if (locationClient.isStarted()) {
            locationClient.stop();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null)
            return;
        LocationData result = new LocationData();
        switch (location.getLocType()) {
        case BDLocation.TypeGpsLocation:
        case BDLocation.TypeCacheLocation:
        case BDLocation.TypeNetWorkLocation:
        case BDLocation.TypeOffLineLocation:
            result.setLongitude(location.getLongitude());
            result.setLatitude(location.getLatitude());
            result.setCountry(DEFAULT_COUNTRY_ZH);
            result.setProvince(location.getProvince());
            result.setCity(location.getCity());
            result.setDistrict(location.getDistrict());
            if (listener != null)
                listener.onLocationUpdate(result);
            stop();
            break;
        default:
            retryCount++;
            if (retryCount > retryTimes) {
                if (listener != null)
                    listener.onLocationError("Fail to update location");
                stop();
            }
            break;
        }
    }

    @Override
    public void onReceivePoi(BDLocation arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isAvailable(Context context) {
        return NetworkUtil.isNetworkConnected(context) || NetworkUtil.isGpsEnable(context);
    }
}
