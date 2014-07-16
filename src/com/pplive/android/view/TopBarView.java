package com.pplive.android.view;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;

public class TopBarView extends RelativeLayout {

    private View mTextTitle;

    private View mBtnLeft;

    private View mBtnRight;

    private String mTitle;

    private boolean mShowLeftBtn;

    private boolean mShowRightBtn;

    private int mLeftBtnResId;

    private int mRightBtnResId;

    private boolean mShowTitle;

    private int mLayoutResId = R.layout.widget_top_bar;

    public TopBarView(Context context) {
        this(context, null);
    }

    public TopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopBarView);

        mShowLeftBtn = a.getBoolean(R.styleable.TopBarView_show_left_btn, false);
        mShowRightBtn = a.getBoolean(R.styleable.TopBarView_show_right_btn, false);

        mLeftBtnResId = a.getResourceId(R.styleable.TopBarView_left_btn_src, 0);
        mRightBtnResId = a.getResourceId(R.styleable.TopBarView_right_btn_src, 0);

        mTitle = a.getString(R.styleable.TopBarView_text);
        mShowTitle = a.getBoolean(R.styleable.TopBarView_show_title, false);

        mLayoutResId = a.getResourceId(R.styleable.TopBarView_layout, R.layout.widget_top_bar);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(mLayoutResId, this, true);

        isInEditMode();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTextTitle = findViewById(R.id.top_bar_text_title);
        mBtnLeft = findViewById(R.id.top_bar_btn_left);
        mBtnRight = findViewById(R.id.top_bar_btn_right);

        init();
    }

    private void init() {
        setLeftBtnImageResource(mLeftBtnResId);
        setRightBtnImageResource(mRightBtnResId);

        if (mShowLeftBtn) {
            showLeftBtn();
        }

        if (mShowRightBtn) {
            showRightBtn();
        }

        setTitle(mTitle);

        if (mShowTitle) {
            showTitle();
        }
    }

    public void setTitle(CharSequence title) {
        if (mTextTitle instanceof TextView) {
            ((TextView) mTextTitle).setText(title);
        }

    }

    public void setTitle(int resId) {
        if (mTextTitle instanceof TextView) {
            ((TextView) mTextTitle).setText(resId);
        }
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
        View btn = null;
        btn = left ? mBtnLeft : mBtnRight;

        if (btn instanceof ImageButton) {
            ((ImageButton) btn).setImageResource(resId);
        }
    }

    public View getLeftBtn() {
        return mBtnLeft;
    }

    public View getRightBtn() {
        return mBtnRight;
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

    public void setLeftBtnEnabled(boolean enabled) {
        setBtnEnabled(true, enabled);
    }

    public void setRightBtnEnabled(boolean enabled) {
        setBtnEnabled(false, enabled);
    }

    private void setBtnEnabled(boolean left, boolean enabled) {
        if (left) {
            mBtnLeft.setEnabled(enabled);
        } else {
            mBtnRight.setEnabled(enabled);
        }
    }

    public void showTitle() {
        mTextTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        mTextTitle.setVisibility(View.GONE);
    }
}
