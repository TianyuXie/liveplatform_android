package com.pplive.liveplatform.ui.widget;

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
        for (int i = 0; i < a.length(); ++i) {
            int attr = a.getIndex(i);

            if (R.styleable.TopBarView_text == attr) {
                mTitle = a.getString(attr);
            } else if (R.styleable.TopBarView_show_left_btn == attr) {
                mShowLeftBtn = a.getBoolean(attr, false);
            } else if (R.styleable.TopBarView_show_right_btn == attr) {
                mShowRightBtn = a.getBoolean(attr, false);
            } else if (R.styleable.TopBarView_left_btn_src == attr) {
                mLeftBtnResId = a.getResourceId(attr, 0);
            } else if (R.styleable.TopBarView_right_btn_src == attr) {
                mRightBtnResId = a.getResourceId(attr, 0);
            } else if (R.styleable.TopBarView_show_title == attr) {
                mShowTitle = a.getBoolean(attr, false);
            } else if (R.styleable.TopBarView_layout == attr) {
                mLayoutResId = a.getResourceId(attr, R.layout.widget_top_bar);
            }
        }

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(mLayoutResId, this, true);
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

    public void showTitle() {
        mTextTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        mTextTitle.setVisibility(View.GONE);
    }
}
