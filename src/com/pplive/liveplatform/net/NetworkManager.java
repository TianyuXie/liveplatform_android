package com.pplive.liveplatform.net;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.pplive.liveplatform.net.event.EventNetworkChanged;
import com.pplive.liveplatform.util.NetworkUtil;

import de.greenrobot.event.EventBus;

public class NetworkManager extends BroadcastReceiver {

    public enum NetworkState {
        WIFI, MOBILE, FAST_MOBILE, DISCONNECTED, UNKNOWN;
    }

    private static final String TAG = NetworkManager.class.getSimpleName();

    private static NetworkState sCurrentNetworkState = NetworkState.UNKNOWN;

    public static void init(Context context) {

        sCurrentNetworkState = getNetworkState(context);

        Log.d(TAG, "Network State: " + sCurrentNetworkState);
    }

    private static NetworkState getNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (NetworkInfo.State.CONNECTED != wifi.getState() && NetworkInfo.State.CONNECTING != wifi.getState()
                && NetworkInfo.State.CONNECTED != mobile.getState() && NetworkInfo.State.CONNECTING != mobile.getState()) {
            return NetworkState.DISCONNECTED;
        } else if (NetworkInfo.State.CONNECTED == wifi.getState() || NetworkInfo.State.CONNECTING == wifi.getState()) {
            return NetworkState.WIFI;
        } else if (NetworkInfo.State.CONNECTED == mobile.getState() || NetworkInfo.State.CONNECTING == mobile.getState()) {
            if (NetworkUtil.isFastMobileNetwork(context)) {
                return NetworkState.FAST_MOBILE;
            } else {
                return NetworkState.MOBILE;
            }
        }

        return NetworkState.UNKNOWN;
    }

    public static NetworkState getCurrentNetworkState() {
        Log.d(TAG, "Current Network State: " + sCurrentNetworkState);
        return sCurrentNetworkState;
    }
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        
        NetworkInfo info = manager.getActiveNetworkInfo();
        
        return null != info && info.isConnectedOrConnecting(); 
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Log.d(TAG, "action: " + intent.getAction());

        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.getAction()) {
            NetworkState state = getNetworkState(context);

            Log.d(TAG, "state: " + state);

            if (sCurrentNetworkState != state) {
                Log.d(TAG, "Network Type Changed!!!");
                EventBus.getDefault().post(new EventNetworkChanged(state));
            }

            if (null != state) {
                sCurrentNetworkState = state;
            }
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.getAction()) {

        }
    }
}
