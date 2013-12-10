package com.pplive.liveplatform.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pplive.liveplatform.R;

public class ShareDialog extends Dialog {
    private String mtitle;

    public ShareDialog(Context context) {
        super(context);
        mtitle = "";
    }

    public ShareDialog(Context context, int theme) {
        this(context, theme, "");
    }

    public ShareDialog(Context context, int theme, String title) {
        super(context, theme);
        mtitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        Button closeBtn = (Button) findViewById(R.id.btn_share_dialog_close);
        TextView titleTextView = (TextView) findViewById(R.id.text_share_dialog_title);
        titleTextView.setText(mtitle);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
