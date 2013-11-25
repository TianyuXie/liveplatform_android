package com.pplive.liveplatform.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pplive.liveplatform.R;

public class ShareDialog extends Dialog {

    public ShareDialog(Context context) {
        super(context);
    }

    public ShareDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        Button closeBtn = (Button) findViewById(R.id.btn_share_dialog_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
