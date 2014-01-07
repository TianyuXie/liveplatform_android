package com.pplive.liveplatform.location;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.pplive.liveplatform.location.Locator.LocationInfo;
import com.pplive.liveplatform.location.Locator.OnLocationUpdateListener;
import com.pplive.liveplatform.location.LocatorFactory.LocatorType;
import com.pplive.liveplatform.util.GeoUtil;

public abstract class LocatorActivity extends FragmentActivity implements OnLocationUpdateListener {
    protected Context context;

    protected Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        try {
            this.locator = LocatorFactory.createLocator(context,
                    GeoUtil.isInChina() ? LocatorType.BAIDU : LocatorType.GOOGLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Locator.LOCATION_SETTINGS_REQUEST_CODE) {
            if (locator.isAvailable(context)) {
                locator.start(context);
            } else {
                onLocationError(null);
            }
        }
    }

    @Override
    protected void onResume() {
        locator.setOnLocationUpdateListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        locator.setOnLocationUpdateListener(null);
        super.onPause();
    }

    public void onClickLocation(View v) {
        locator.stop();
        locator.start(context);
    }

    @Override
    public void onLocationUpdate(LocationInfo location) {
        if (location == null) {
            return;
        }
    }

    @Override
    public void onLocationError(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

}
