package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.attr.ISelfHidable;

public class LoadingButton extends RelativeLayout implements ISelfHidable {
    private ViewGroup mRoot;

    private Button mBaseButton;

    private ImageView mAnimImageView;

    private TextView mStatusTextView;

    private Animation mAnimation;

    private boolean mShowing;

    private boolean mLoading;

    private int mNormalBackgroundRes;

    private int mLoadingBackgroundRes;

    private CharSequence mNormalText;

    public LoadingButton(Context context) {
        this(context, null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (ViewGroup) inflater.inflate(R.layout.widget_loadingbutton, this);
        mBaseButton = (Button) mRoot.findViewById(R.id.btn_loading_base);
        mAnimImageView = (ImageView) mRoot.findViewById(R.id.image_loading_anim);
        mStatusTextView = (TextView) mRoot.findViewById(R.id.textview_loading_status);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton);
        float size = typedArray.getDimensionPixelSize(R.styleable.LoadingButton_textSize, 20);
        typedArray.recycle();

        mStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

        mAnimImageView.setVisibility(INVISIBLE);
        mShowing = (getVisibility() == VISIBLE);
        mNormalText = "";
    }

    public void setText(CharSequence text) {
        mNormalText = text;
        mStatusTextView.setText(text);
    }

    public void setText(int resid) {
        setText(getContext().getResources().getString(resid));
    }

    public void setTextSize(float size) {
        mStatusTextView.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        mStatusTextView.setTextSize(unit, size);
    }

    public void setBackgroundResource(int normal, int loading) {
        mNormalBackgroundRes = normal;
        mLoadingBackgroundRes = loading;
        mBaseButton.setBackgroundResource(normal);
    }

    public void setAnimation(int id) {
        mAnimation = AnimationUtils.loadAnimation(getContext(), id);
    }

    public void setAnimation(Animation animation) {
        mAnimation = animation;
    }

    public void startLoading(int resid) {
        startLoading(getContext().getResources().getText(resid));
    }

    public void startLoading(CharSequence text) {
        if (!mLoading && mAnimation != null) {
            mLoading = true;
            mStatusTextView.setText(text);
            mAnimImageView.setVisibility(VISIBLE);
            mAnimImageView.startAnimation(mAnimation);
            mBaseButton.setBackgroundResource(mLoadingBackgroundRes);
        }
    }

    public void showLoadingResult(int id) {
        showLoadingResult(getContext().getResources().getText(id));
    }

    public void showLoadingResult(CharSequence text) {
        if (mLoading) {
            mLoading = false;
            mStatusTextView.setText(text);
            mAnimImageView.setVisibility(INVISIBLE);
            mAnimImageView.clearAnimation();
            mBaseButton.setBackgroundResource(mLoadingBackgroundRes);
        }
    }

    public void finishLoading() {
        if (mLoading) {
            mLoading = false;
            mAnimImageView.setVisibility(INVISIBLE);
            mAnimImageView.clearAnimation();
        }
        mStatusTextView.setText(mNormalText);
        mBaseButton.setBackgroundResource(mNormalBackgroundRes);
    }

    @Override
    public void hide(boolean gone) {
        if (mShowing) {
            mRoot.setVisibility(gone ? GONE : INVISIBLE);
            mShowing = false;
        }
    }

    @Override
    public void hide() {
        hide(false);
    }

    @Override
    public void show() {
        if (!mShowing) {
            mRoot.setVisibility(VISIBLE);
            mShowing = true;
        }
    }

    @Override
    @Deprecated
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            mShowing = true;
        } else {
            mShowing = false;
        }
        super.setVisibility(visibility);
    }
}
