package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.ui.LoginActivity;
import com.pplive.liveplatform.ui.SettingsActivity;
import com.pplive.liveplatform.ui.UserpageActivity;
import com.pplive.liveplatform.ui.widget.attr.IHidable;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;

public class SideBar extends LinearLayout implements SlidableContainer.OnSlideListener, IHidable {
    static final String TAG = "_SideBar";

    private static final DisplayImageOptions DEFAULT_ICON_DISPLAY_OPTIONS = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.user_icon_default).showImageForEmptyUri(R.drawable.user_icon_default).showStubImage(R.drawable.user_icon_default)
            .cacheOnDisc(true).build();

    private View mRoot;

    private View mBlockLayer;

    private RadioGroup mRadioGroup;

    private Animation mShowAnimation;

    private Animation mHideAnimation;

    private boolean mAnimating;

    private boolean mShowing;

    private TextView mNicknameText;

    private CircularImageView mUserIcon;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = inflater.inflate(R.layout.widget_sidebar, this);

        int id = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideBar);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.SideBar_anim_show:
                id = a.getResourceId(attr, -1);
                if (id > 0) {
                    mShowAnimation = AnimationUtils.loadAnimation(context, id);
                }
                break;
            case R.styleable.SideBar_anim_hide:
                id = a.getResourceId(attr, -1);
                if (id > 0) {
                    mHideAnimation = AnimationUtils.loadAnimation(context, id);
                }
                break;
            }
        }
        a.recycle();
        mShowing = (getVisibility() == VISIBLE);
        mRadioGroup = (RadioGroup) mRoot.findViewById(R.id.radiogroup_sidebar_type);
        mNicknameText = (TextView) mRoot.findViewById(R.id.text_sidebar_user);
        mUserIcon = (CircularImageView) mRoot.findViewById(R.id.btn_sidebar_user_icon);
        mBlockLayer = mRoot.findViewById(R.id.layout_block_layer);
        mUserIcon.setOnClickListener(onUserBtnClickListener);
        mRoot.findViewById(R.id.btn_sidebar_settings).setOnClickListener(onSettingsBtnClickListener);
    }

    public void updateUsername() {
        if (UserManager.getInstance(getContext()).isLogin()) {
            mNicknameText.setText(UserManager.getInstance(getContext()).getNickname());
            String iconUrl = UserManager.getInstance(getContext()).getIcon();
            Log.d(TAG, iconUrl);
            mUserIcon.setRounded(false);
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserIcon.setImageAsync(iconUrl, DEFAULT_ICON_DISPLAY_OPTIONS, imageLoadingListener);
            } else {
                mUserIcon.setImageResource(R.drawable.user_icon_default);
            }
        } else {
            mNicknameText.setText("");
            mUserIcon.setImageResource(R.drawable.user_icon_login);
            mUserIcon.setRounded(false);
        }
    }

    @Override
    public void hide(boolean gone) {
        if (!mAnimating && mShowing && mHideAnimation != null) {
            startAnimation(mHideAnimation);
            mRoot.setVisibility(gone ? GONE : INVISIBLE);
            mBlockLayer.setClickable(true);
            setClickable(false);
            mShowing = false;
        }
    }

    @Override
    public void show() {
        if (!mAnimating && !mShowing && mShowAnimation != null) {
            mRoot.setVisibility(VISIBLE);
            setClickable(true);
            mBlockLayer.setClickable(false);
            mShowing = true;
            startAnimation(mShowAnimation);
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

    @Override
    public void startAnimation(Animation animation) {
        mAnimating = true;
        super.startAnimation(animation);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        mAnimating = false;
    }

    @Override
    public void onSlide() {
        show();
    }

    @Override
    public void onSlideBack() {
        hide(true);
    }

    @Override
    public void hide() {
        hide(true);
    }

    public void setOnTypeChangeListener(RadioGroup.OnCheckedChangeListener l) {
        mRadioGroup.setOnCheckedChangeListener(l);
    }

    private View.OnClickListener onSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            getContext().startActivity(intent);
        }
    };

    private View.OnClickListener onUserBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(getContext()).isLogin()) {
                Intent intent = new Intent(getContext(), UserpageActivity.class);
                getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_TAGET, UserpageActivity.class.getName());
                getContext().startActivity(intent);
            }
        }
    };

    private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
            Log.d(TAG, "onLoadingStarted");
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            Log.d(TAG, "onLoadingFailed");
            mUserIcon.setRounded(false);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.d(TAG, "onLoadingComplete");
            mUserIcon.setRounded(arg2 != null);
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            Log.d(TAG, "onLoadingCancelled");
            mUserIcon.setRounded(false);
        }
    };

    public void release() {
        mUserIcon.release();
    }
}
