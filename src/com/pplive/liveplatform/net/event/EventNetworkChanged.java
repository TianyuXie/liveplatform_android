package com.pplive.liveplatform.net.event;

import com.pplive.liveplatform.net.NetworkManager.NetworkState;

public class EventNetworkChanged {

    NetworkState mState = NetworkState.UNKNOWN;
    
    public EventNetworkChanged(NetworkState state) {
        mState = state;
    }
    
    public NetworkState getNetworkState() {
        return mState;
    }
}
