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

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (null == networkInfo || !networkInfo.isConnected() || networkInfo.isRoaming()) {
            return NetworkState.DISCONNECTED;
        } else {
            NetworkInfo.State state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (NetworkInfo.State.CONNECTED == state || NetworkInfo.State.CONNECTING == state) {
                return NetworkState.WIFI;
            }

            state = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (NetworkInfo.State.CONNECTED == state || NetworkInfo.State.CONNECTING == state) {
                if (NetworkUtil.isFastMobileNetwork(context)) {
                    return NetworkState.FAST_MOBILE;
                } else {
                    return NetworkState.MOBILE;
                }
            }
        }

        return NetworkState.UNKNOWN;
    }

    public static NetworkState getCurrentNetworkState() {
        return sCurrentNetworkState;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Log.d(TAG, "action: " + intent.getAction());

        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.getAction()) {
            NetworkState state = getNetworkState(context);
            
            if (sCurrentNetworkState != state && NetworkState.WIFI != state) {
                Log.d(TAG, "Network Type Changed!!!");

                EventBus.getDefault().post(new EventNetworkChanged(state));
            }

            sCurrentNetworkState = state;
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.getAction()) {

        }
    }
}
