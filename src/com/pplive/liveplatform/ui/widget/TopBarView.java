package com.pplive.liveplatform.ui.widget;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;

public class TopBarView extends RelativeLayout {

    private TextView mTextTitle;

    private ImageButton mBtnLeft;

    private ImageButton mBtnRight;

    private String mTitle;

    public TopBarView(Context context) {
        this(context, null);
    }

    public TopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_top_bar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopBarView);
        for (int i = 0; i < a.length(); ++i) {
            int attr = a.getIndex(i);

            if (attr == R.styleable.TopBarView_text) {
                mTitle = a.getString(attr);
            }
        }

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTextTitle = (TextView) findViewById(R.id.top_bar_text_title);
        mBtnLeft = (ImageButton) findViewById(R.id.top_bar_btn_left);
        mBtnRight = (ImageButton) findViewById(R.id.top_bar_btn_right);

        init();
    }

    private void init() {
        setLeftBtnImageResource(R.drawable.top_bar_back_btn);

        if (TextUtils.isEmpty(mTitle)) {
            setTitle(R.string.app_name);
        } else {
            setTitle(mTitle);
        }
    }

    public void setTitle(CharSequence title) {
        mTextTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTextTitle.setText(resId);
    }

    public void setLeftBtnOnClickListener(View.OnClickListener listener) {
        setBtnOnClickListener(true, listener);
    }

    public void setRightBtnOnClickListener(View.OnClickListener listener) {
        setBtnOnClickListener(false, listener);
    }

    private void setBtnOnClickListener(boolean left, View.OnClickListener listener) {
        if (left) {
            mBtnLeft.setOnClickListener(listener);
        } else {
            mBtnRight.setOnClickListener(listener);
        }
    }

    public void setLeftBtnImageResource(int resId) {
        setBtnImageResource(true, resId);
    }

    public void setRightBtnImageResource(int resId) {
        setBtnImageResource(false, resId);
    }

    private void setBtnImageResource(boolean left, int resId) {
        if (left) {
            mBtnLeft.setImageResource(resId);
        } else {
            mBtnRight.setImageResource(resId);
        }
    }

    public void hideLeftBtn() {
        hideBtn(true);
    }

    public void hideRightBtn() {
        hideBtn(false);
    }

    private void hideBtn(boolean left) {
        if (left) {
            mBtnLeft.setVisibility(View.GONE);
        } else {
            mBtnRight.setVisibility(View.GONE);
        }
    }

    public void showLeftBtn() {
        showBtn(true);
    }

    public void showRightBtn() {
        showBtn(false);
    }

    private void showBtn(boolean left) {
        if (left) {
            mBtnLeft.setVisibility(View.VISIBLE);
        } else {
            mBtnRight.setVisibility(View.VISIBLE);
        }
    }
}
