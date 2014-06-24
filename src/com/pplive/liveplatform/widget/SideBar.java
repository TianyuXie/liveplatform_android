package com.pplive.liveplatform.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.ui.LoginActivity;
import com.pplive.liveplatform.ui.SettingsActivity;
import com.pplive.liveplatform.ui.UserpageActivity;
import com.pplive.liveplatform.widget.attr.IHidable;
import com.pplive.liveplatform.widget.image.CircularImageView;
import com.pplive.liveplatform.widget.slide.SlidableContainer;

public class SideBar extends LinearLayout implements SlidableContainer.OnSlideListener, IHidable {
    static final String TAG = "_SideBar";

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
            if (!TextUtils.isEmpty(iconUrl)) {
                mUserIcon.setImageAsync(iconUrl, R.drawable.user_icon_default);
            } else {
                mUserIcon.setLocalImage(R.drawable.user_icon_default, true);
            }
        } else {
            mNicknameText.setText("");
            mUserIcon.setLocalImage(R.drawable.user_icon_login, true);
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
            if (UserManager.getInstance(getContext()).isLoginSafely()) {
                Intent intent = new Intent(getContext(), UserpageActivity.class);
                intent.putExtra(UserpageActivity.EXTRA_USER, UserManager.getInstance(getContext()).getUsernamePlain());
                intent.putExtra(UserpageActivity.EXTRA_ICON, UserManager.getInstance(getContext()).getIcon());
                intent.putExtra(UserpageActivity.EXTRA_NICKNAME, UserManager.getInstance(getContext()).getNickname());
                getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra(Extra.KEY_REDIRECT, UserpageActivity.class);
                getContext().startActivity(intent);
            }
        }
    };

    public void release() {
        mUserIcon.release();
    }
}
