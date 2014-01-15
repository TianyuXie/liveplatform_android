package com.pplive.liveplatform.location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.pplive.liveplatform.location.Locator.OnLocationUpdateListener;
import com.pplive.liveplatform.location.LocatorFactory.LocatorType;
import com.pplive.liveplatform.util.GeoUtil;

public abstract class LocatorActivity extends FragmentActivity implements OnLocationUpdateListener {

    static final String TAG = "_LocatorActivity";

    private Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locator = LocatorFactory.createLocator(this, GeoUtil.isInChina() ? LocatorType.BAIDU : LocatorType.GOOGLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locator.setOnLocationUpdateListener(this);
    }

    @Override
    protected void onPause() {
        locator.setOnLocationUpdateListener(null);
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        locator.stop();
        super.onStop();
    }

    protected void startLocator() {
        Log.d(TAG, "startLocator");
        locator.stop();
        locator.start(this);
    }
}
