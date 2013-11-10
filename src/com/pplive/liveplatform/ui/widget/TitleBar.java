package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;

public class TitleBar extends LinearLayout {
    private ToggleButton mMenuButton;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.widget_titlebar, this);
        mMenuButton = (ToggleButton) root.findViewById(R.id.btn_titlebar_menu);
    }

    public void setMenuButtonOnClickListener(View.OnClickListener menuButtonOnClickListener) {
        mMenuButton.setOnClickListener(menuButtonOnClickListener);
    }

    public void setMenuButtonHighlight(boolean isOn) {
        mMenuButton.setChecked(isOn);
    }
}
