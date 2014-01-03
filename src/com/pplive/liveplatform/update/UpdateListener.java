package com.pplive.liveplatform.update;

import java.util.ArrayList;

public abstract class UpdateListener
{
    public abstract void onCompleted(ArrayList<UpdateInfo> updateInfos);

    public void onFailure()
    {
    }
}
