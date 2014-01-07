package com.pplive.liveplatform.location;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.pplive.liveplatform.util.NetworkUtil;
import com.pplive.liveplatform.util.StringUtil;

public class GoogleLocator extends Locator implements LocationListener {
    static final String TAG = "GoogleLocator";

    private static final int MSG_TIMEOUT = 9001;

    private static final int MIN_DISTANCE = 100;

    private static final int MAX_GEOCODER_RESULTS = 5;

    private LocationManager locationManager;

    private Geocoder geocoder;

    private Timer timer;

    private int gpsTimeout = 200000;

    private int networkTimeout = 30000;

    private int timeout;

    public GoogleLocator(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.geocoder = new Geocoder(context);
    }

    @Override
    public boolean setProvider(Context context) {
        Provider newProvider;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && NetworkUtil.isWifiConnected(context)) {
            newProvider = Provider.WIFI;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && NetworkUtil.isNetworkConnected(context)) {
            newProvider = Provider.MOBILE;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            newProvider = Provider.GPS;
        } else {
            newProvider = Provider.NONE;
        }
        boolean result = (!newProvider.equals(this.provider));
        this.provider = newProvider;
        return result;
    }

    @Override
    public void start(Context context) {
        Log.d(TAG, "start");
        setProvider(context);
        switch (this.provider) {
        case WIFI:
        case MOBILE:
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    networkScanSpan, MIN_DISTANCE, this);
            timeout = networkTimeout;
            break;
        case GPS:
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsScanSpan,
                    MIN_DISTANCE, this);
            timeout = gpsTimeout;
            break;
        default:
            break;
        }
        if (this.provider == Provider.NONE) {
            showDialog(context);
            stop();
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(MSG_TIMEOUT);
                }
            }, timeout);
        }
    }

    private void showDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Location Service");
        builder.setMessage("Do you want to open the service?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((Activity) context).startActivityForResult(new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SETTINGS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (listener != null)
                    listener.onLocationError("Fail to update location");
            }
        });
        builder.show();
    }

    @Override
    public void stop() {
        locationManager.removeUpdates(this);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_TIMEOUT:
                if (listener != null)
                    listener.onLocationError("Fail to update location");
                stop();
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        Log.d(TAG, location.toString());
        LocationInfo result = new LocationInfo();
        result.setLatitude(location.getLatitude());
        result.setLongitude(location.getLongitude());
        try {
            Address address = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), MAX_GEOCODER_RESULTS).get(0);
            Log.d(TAG, address.toString());
            result.setCountry(StringUtil.safeString(address.getCountryName()));
            result.setProvince(StringUtil.safeString(address.getAdminArea()));
            result.setCity(StringUtil.safeString(address.getLocality()));
            result.setDistrict(StringUtil.safeString(address.getSubLocality()));
        } catch (Exception e) {
            result.setCountry("");
            result.setProvince("");
            result.setCity("");
            result.setDistrict("");
        }

        if (listener != null) {
            listener.onLocationUpdate(result);
        }
        stop();
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isAvailable(Context context) {
        return (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && NetworkUtil
                .isNetworkConnected(context))
                || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
