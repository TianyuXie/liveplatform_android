package com.pplive.liveplatform.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.pplive.liveplatform.R;

public class IconDialog extends Dialog {
    static final String TAG = "_IconDialog";
    
    private OnClickListener mOnCameraClickListener;

    public IconDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_icon);
    }

}
