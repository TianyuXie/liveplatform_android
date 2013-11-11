package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;

public class TitleBar extends LinearLayout {
    private ToggleButton mMenuButton;

    private Button mSearchButton;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.widget_titlebar, this);
        mMenuButton = (ToggleButton) root.findViewById(R.id.btn_titlebar_menu);
        mSearchButton = (Button) root.findViewById(R.id.btn_titlebar_search);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mMenuButton.setOnClickListener(l);
        mSearchButton.setOnClickListener(l);
    }

    public void setMenuButtonHighlight(boolean isOn) {
        mMenuButton.setChecked(isOn);
    }
}
