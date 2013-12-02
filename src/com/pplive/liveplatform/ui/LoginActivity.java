package com.pplive.liveplatform.ui;

import com.pplive.liveplatform.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login_back).setOnClickListener(onBackBtnClickListener);
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
