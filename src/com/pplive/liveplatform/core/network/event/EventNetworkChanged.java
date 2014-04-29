package com.pplive.liveplatform.core.network.event;

import com.pplive.liveplatform.core.network.NetworkManager.NetworkState;

public class EventNetworkChanged {

    NetworkState mState = NetworkState.UNKNOWN;
    
    public EventNetworkChanged(NetworkState state) {
        mState = state;
    }
    
    public NetworkState getNetworkState() {
        return mState;
    }
}
