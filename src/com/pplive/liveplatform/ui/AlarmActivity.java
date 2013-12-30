package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pplive.liveplatform.R;

public class AlarmActivity extends Activity {
    public final static String EXTRA_TITLE = "extra_title";

    public final static String EXTRA_MESSAGE = "extra_message";

    public final static String EXTRA_PROGRAM = "extra_program";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prelive_alarm);

        findViewById(R.id.btn_dialog_confirm).setOnClickListener(onConfirmClickListener);
        findViewById(R.id.btn_dialog_cancel).setOnClickListener(onCancelClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView title = (TextView) findViewById(R.id.text_dialog_title);
        TextView message = (TextView) findViewById(R.id.text_dialog_message);
        title.setText(getIntent().getStringExtra(EXTRA_TITLE));
        message.setText(getIntent().getStringExtra(EXTRA_MESSAGE));
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AlarmActivity.this, LiveRecordActivity.class);
            intent.putExtra(LiveRecordActivity.EXTRA_PROGRAM, getIntent().getSerializableExtra(EXTRA_PROGRAM));
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener onCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
