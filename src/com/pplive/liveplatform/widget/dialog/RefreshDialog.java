package com.pplive.liveplatform.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.pplive.liveplatform.R;

public class RefreshDialog extends Dialog {

    public RefreshDialog(Context context) {
        super(context, R.style.refresh_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_refresh);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}
