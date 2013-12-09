package com.pplive.liveplatform.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.LoadingButton;

public class LoadingDialog extends Dialog {
    private LoadingButton mButton;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
        mButton = (LoadingButton) findViewById(R.id.btn_loading_status);
    }

    @Override
    public void show() {
        super.show();
        mButton.startLoading(R.string.player_loading);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mButton.finishLoading();
    }
}
