package com.pplive.liveplatform.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.pplive.liveplatform.R;

public class IconDialog extends Dialog {
    static final String TAG = "_IconDialog";

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

    public void setOnCameraClickListener(android.view.View.OnClickListener l) {
        findViewById(R.id.btn_userpage_icon_camera).setOnClickListener(l);
    }

    public void setOnGalleryClickListener(android.view.View.OnClickListener l) {
        findViewById(R.id.btn_userpage_icon_gallery).setOnClickListener(l);
    }

}
