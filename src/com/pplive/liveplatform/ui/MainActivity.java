package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pplive.liveplatform.R;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_go_player:
            Intent intent = new Intent(getApplicationContext(), LivePlayerActivity.class);
            startActivity(intent);
            break;
        case R.id.btn_go_recorder:
            intent = new Intent(getApplicationContext(), LiveRecordActivity.class);
            startActivity(intent);
            break;
        case R.id.btn_go_home:
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        default:
            break;
        }
    }

}
