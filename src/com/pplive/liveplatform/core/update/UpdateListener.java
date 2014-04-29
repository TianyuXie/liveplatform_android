package com.pplive.liveplatform.core.update;

import java.util.ArrayList;

public abstract class UpdateListener {
    public abstract void onCompleted(ArrayList<UpdateInfo> updateInfos);

    public void onFailure() {
    }
}
