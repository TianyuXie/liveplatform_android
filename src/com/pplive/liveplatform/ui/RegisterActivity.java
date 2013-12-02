package com.pplive.liveplatform.ui;

import com.pplive.liveplatform.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        findViewById(R.id.btn_register_back).setOnClickListener(onBackBtnClickListener);
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
