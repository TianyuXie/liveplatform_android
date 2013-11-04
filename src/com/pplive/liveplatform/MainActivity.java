package com.pplive.liveplatform;

import com.pplive.liveplatform.ui.LivePlayerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(this, LivePlayerActivity.class);
        startActivity(intent);
    }
}
