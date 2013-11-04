package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.pplive.liveplatform.R;

public class LiveRecorderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_live_recorder);
        
        
    }

}
